/**
 * Copyright (C) 2013 Oliver Schünemann
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, 
 * Boston, MA 02110, USA 
 * 
 * @since 28.09.2013
 * @version 1.0
 * @author oliver
 */
package pb.web.simple;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.References;
import org.apache.felix.scr.annotations.Service;

import pb.model.FavoriteModel;
import pb.model.FavoriteModel.Voting;
import pb.persistence.PbStorage;
import pb.persistence.entity.Candidate;
import pb.persistence.entity.Candidate.Gender;
import pb.persistence.entity.Credit;

/**
 * @author oliver
 * 
 */
@Component(name = "pb.web.list", immediate = true, label = "pb.web.list")
@Service(value = HttpServlet.class)
@Properties(value = { @Property(name = "alias", value = "/simple") })
@References({
		@Reference(name = "PBStorage", bind = "bind", unbind = "unbind", referenceInterface = PbStorage.class, cardinality = ReferenceCardinality.MANDATORY_UNARY, policy = ReferencePolicy.DYNAMIC),
		@Reference(name = "FavoritModel", bind = "bind", unbind = "unbind", referenceInterface = FavoriteModel.class, cardinality = ReferenceCardinality.MANDATORY_UNARY, policy = ReferencePolicy.DYNAMIC) })
@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public class ListServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ReentrantLock lock = new ReentrantLock();
	protected PbStorage storage = null;
	protected FavoriteModel model = null;

	/**
	 * returns a Html Page with a list of Candidates
	 * 
	 * @param dataSet
	 *            DataSet of candidates to display
	 * @return string containing an HTML page
	 */
	protected String getHtml(final List<Candidate> dataSet) {
		final StringBuilder builder = new StringBuilder();
		builder.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n");
		builder.append("<html>\r\n");
		builder.append("<head>\r\n");
		builder.append("  <meta content=\"text/html; charset=UTF-8\"\r\n");
		builder.append(" http-equiv=\"content-type\">\r\n");
		builder.append("  <title>List of Candidates</title>\r\n");
		builder.append("</head>\r\n");
		builder.append("<body>\r\n");
		builder.append("<center>\r\n");
		builder.append("<table border=\"1\" width=\"90%\" cellspacing=\"0\" rules=\"all\">\r\n");
		builder.append("<colgroup>\r\n");
		builder.append("  <col width=\"160\">\r\n");
		builder.append("  <col width=\"35%\">\r\n");
		builder.append("  <col width=\"55%\">\r\n");
		builder.append("  <col width=\"15%\">\r\n");
		builder.append("</colgroup>\r\n");

		for (final Candidate candidate : dataSet) {
			builder.append("<tr>\r\n");
			builder.append(" <td><img src=\"avatar.png?id=" + candidate.getId()
					+ "\" alt=\"Candidate " + candidate.getName()
					+ "\" width=\"160\" height=\"160\"></td>\r\n");
			builder.append(" <td>" + candidate.getName() + "</td>\r\n");
			builder.append(" <td>" + candidate.getShortDescription() + "</td>\r\n");
			builder.append(" <td>" + candidate.getAge() + "</td>\r\n");
			builder.append("</tr>\r\n");
		}
		builder.append("</table>\r\n");
		builder.append("</center>\r\n");
		builder.append("</body>\r\n");
		builder.append("</html>\r\n");
		return builder.toString();
	}

	/**
	 * returns a Html Page with a list of the votes of a candidate
	 * 
	 * @param dataSet
	 *            DataSet of candidates to display
	 * @return string containing an HTML page
	 */
	protected String getVotingHtml(final List<Voting> dataSet, final int credits) {
		final StringBuilder builder = new StringBuilder();
		builder.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n");
		builder.append("<html>\r\n");
		builder.append("<head>\r\n");
		builder.append("  <meta content=\"text/html; charset=UTF-8\"\r\n");
		builder.append(" http-equiv=\"content-type\">\r\n");
		builder.append("  <title>List of Votes</title>\r\n");
		builder.append("</head>\r\n");
		builder.append("<body>\r\n");

		builder.append("<center>\r\n");
		builder.append("<h1>Credits left " + credits + "</h1>\r\n");
		builder.append("<table border=\"1\" width=\"90%\" cellspacing=\"0\" rules=\"all\">\r\n");
		builder.append("<colgroup>\r\n");
		builder.append("  <col width=\"160\">\r\n");
		builder.append("  <col width=\"50%\">\r\n");
		builder.append("  <col width=\"50%\">\r\n");
		builder.append("</colgroup>\r\n");

		for (final Voting voting : dataSet) {
			String bgColor = "\"#ffffff\"";
			if (voting.isPair()) {
				bgColor = "\"#00ff00\"";
			}
			builder.append("<tr>\r\n");
			builder.append(" <td bgcolor=" + bgColor + "><img src=\"avatar.png?id="
					+ voting.getOtherId()
					+ "\" alt=\"Candidate\" width=\"160\" height=\"160\"></td>\r\n");
			builder.append(" <td bgcolor=" + bgColor + ">" + voting.getOwnLikes() + "</td>\r\n");
			builder.append(" <td bgcolor=" + bgColor + ">" + voting.getOtherLikes() + "</td>\r\n");
			builder.append("</tr>\r\n");
		}
		builder.append("</table>\r\n");
		builder.append("</center>\r\n");
		builder.append("</body>\r\n");
		builder.append("</html>\r\n");
		return builder.toString();
	}

	/**
	 * returns an HTML page containing the given error message
	 * 
	 * @param message
	 *            the error message to instert into the page
	 * @return
	 */
	protected String getErrorMessage(final String message) {
		final StringBuilder builder = new StringBuilder();
		builder.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n");
		builder.append("<html>\r\n");
		builder.append("<head>\r\n");
		builder.append("  <meta content=\"text/html; charset=UTF-8\"\r\n");
		builder.append(" http-equiv=\"content-type\">\r\n");
		builder.append("  <title>Error</title>\r\n");
		builder.append("</head>\r\n");
		builder.append("<body>\r\n");
		builder.append("<h1>Error occured</h1>\r\n");
		builder.append("<br/>\r\n");
		builder.append(message);
		builder.append("<br/>\r\n");
		builder.append("</body>\r\n");
		builder.append("</html>\r\n");
		return builder.toString();
	}

	/**
	 * A HTML Page containing only a Success String
	 * 
	 * @param message
	 *            the success message to be contained in the HTML Page
	 * @return
	 */
	protected String getSuccessMessage(final String message) {
		final StringBuilder builder = new StringBuilder();
		builder.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n");
		builder.append("<html>\r\n");
		builder.append("<head>\r\n");
		builder.append("<meta content=\"text/html; charset=UTF-8\"\r\n");
		builder.append(" http-equiv=\"content-type\">\r\n");
		builder.append("  <title>Success</title>\r\n");
		builder.append("</head>\r\n");
		builder.append("<body>\r\n");
		builder.append("<br/>\r\n");
		builder.append("<h1>Success</h1>\r\n");
		builder.append(message);
		builder.append("<br/>\r\n");
		builder.append("</body>\r\n");
		builder.append("</html>\r\n");
		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			final int id = Integer.parseInt(req.getParameter("id"));
			if (req.getPathInfo().equalsIgnoreCase("/candidates.html")) {
				if (storage == null) {
					resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				} else {
					final Candidate requester = storage.getCandidate(id);
					final List<Candidate> candidates = storage.getCandidateByGender(Gender.MALE
							.equals(requester.getGender()) ? Gender.FEMALE : Gender.MALE);
					resp.getOutputStream().write(getHtml(candidates).getBytes());
				}
				resp.setContentType("text/html");
				resp.setStatus(HttpServletResponse.SC_OK);

			} else if (req.getPathInfo().equalsIgnoreCase("/votes.html")) {
				if ((model == null) || (storage == null)) {
					resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				} else {
					final Credit credit = storage.getCredit(id);
					final List<Voting> votings = model.getVotings(id);
					resp.getOutputStream().write(
							getVotingHtml(votings, credit.getCredit()).getBytes());
				}
				resp.setContentType("text/html");
				resp.setStatus(HttpServletResponse.SC_OK);

			} else if (req.getPathInfo().equalsIgnoreCase("/avatar.png")) {
				final Candidate candidate = storage.getCandidate(id);
				if ((storage == null) || (candidate == null)) {
					resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				} else {
					resp.getOutputStream().write(candidate.getImage());
					resp.setContentType("image/png");
				}
			} else {
				resp.setContentType("text/html");
				resp.getOutputStream().write(getErrorMessage("Unknown Request").getBytes());
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
			resp.getOutputStream().close();
		} catch (final Throwable e) {
			Log.error("Error occured while Processing Request", e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getOutputStream().write(getErrorMessage("Unknown Error Occured").getBytes());
			resp.getOutputStream().close();
		}
	}

	/**
	 * removes xml tags from the string to avoid cross site scripting
	 * 
	 * @param html
	 *            the string to remove xml tags from
	 * @return the hopefully more harmless text
	 */
	private String removeHtml(final String html) {
		String ret = html;
		final String replaces[][] = { { "&", "&amp;" }, { "<", "&lt;" }, { ">", "&gt;" } };

		if (ret != null) {
			for (int i = 0; i < replaces.length; i++) {
				ret = ret.replaceAll(replaces[i][0], replaces[i][1]);
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		final String pathInfo = req.getPathInfo();
		Log.info("Do Post received Request \"%s\"", pathInfo);
		try {
			if (pathInfo.equalsIgnoreCase("/newProfile.html") && (storage != null)) {
				final int id = storage.getId(req.getRemoteAddr());
				Candidate candidate = storage.getCandidate(id);
				if (candidate == null) {
					candidate = new Candidate();
					candidate.setId(id);
				}

				final ServletFileUpload fileUpload = new ServletFileUpload();
				for (final FileItemIterator iter = fileUpload.getItemIterator(req); iter.hasNext();) {
					final FileItemStream item = iter.next();
					if (item.isFormField()) {
						final String value = Streams.asString(item.openStream());
						final String key = item.getFieldName();
						if ("Name".equals(key)) {
							candidate.setName(removeHtml(value));
						} else if ("Age".equals(key)) {
							try {
								candidate.setAge(Integer.parseInt(value.trim()));
							} catch (final Throwable thr) {
								Log.warning("Wrong Age Format", thr);
							}
						} else if ("Description".equals(key)) {
							candidate.setShortDescription(removeHtml(value));
						} else if ("GenderGroup".equals(key)) {
							if ("Male".equals(value)) {
								candidate.setGender(Gender.MALE);
							} else {
								candidate.setGender(Gender.FEMALE);
							}
						}
					} else {
						Log.info("Content-type: %s", item.getContentType());
						final InputStream is = item.openStream();
						final ByteArrayOutputStream os = new ByteArrayOutputStream();
						final byte b[] = new byte[12000];
						int got = is.read(b);
						while (got > 0) {
							os.write(b, 0, got);
							got = is.read(b);
						}
						is.close();
						candidate.setImage(os.toByteArray());
					}
				}
				storage.storeCandidate(candidate);
				Log.info("Candidate is stored");
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.setContentType("text/html");
				final OutputStream os = resp.getOutputStream();
				os.write(getSuccessMessage("Profile changed").getBytes());
				os.close();
			} else {
				resp.setContentType("text/html");
				resp.getOutputStream().write(getErrorMessage("Unknown Request").getBytes());
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				resp.getOutputStream().close();
			}
		} catch (final Throwable e) {
			Log.error("Error in doPost occured", e);
		}
	}

	/**
	 * Bind Method for the Persistent Storage
	 * 
	 * @param storage
	 */
	protected void bind(final PbStorage storage) {
		this.storage = storage;
	}

	/**
	 * Unbind Method for the Persistent Storage
	 * 
	 * @param storage
	 */
	protected void unbind(final PbStorage storage) {
		if (storage.equals(this.storage)) {
			this.storage = null;
		}
	}

	/**
	 * Bind method for the Party Model
	 * 
	 * @param model
	 */
	protected void bind(final FavoriteModel model) {
		this.model = model;
	}

	/**
	 * Unbind method for the Party Model
	 * 
	 * @param model
	 */
	protected void unbind(final FavoriteModel model) {
		if (model.equals(this.model)) {
			this.model = null;
		}
	}
}
