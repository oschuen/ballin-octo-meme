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
 * @since 29.10.2013
 * @version 1.0
 * @author oliver
 */
package pb.web.gwt.client.comp;

import pb.web.gwt.client.AppPart;
import pb.web.gwt.client.Messages;
import pb.web.gwt.client.ProfileServiceAsync;
import pb.web.gwt.shared.Profile;
import pb.web.gwt.shared.ProfileFormFields;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Composite containing the Profile Edit Page. Putting the content in an extra
 * Class derived from Composite allows to design it with the WindowBuilder
 * 
 * This page is needed to set up the profile of this member
 * 
 * @author oliver
 */
public class ProfileComposite extends Composite {
	private final Image image = new Image("");
	private final TextBox nameTextBox = new TextBox();
	private final TextArea descriptionTextArea = new TextArea();
	private final IntegerBox ageIntegerBox = new IntegerBox();
	private final RadioButton maleRadioButton;
	private final RadioButton femaleRadioButton;

	private final Messages messages;

	private final ProfileServiceAsync profileService;

	/**
	 * Constructor taking service and messages object from parent
	 * 
	 * @param profileService
	 * @param messages
	 */
	@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
	public ProfileComposite(final ProfileServiceAsync profileService, final Messages messages) {
		this.profileService = profileService;
		this.messages = messages;
		final VerticalPanel verticalPanel = new VerticalPanel();
		initWidget(verticalPanel);
		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setStyleName("formPanel");
		verticalPanel.setSize("100%", "100%");

		verticalPanel.add(image);

		final FormPanel formPanel = new FormPanel();
		formPanel.setStyleName("formPanel");
		verticalPanel.add(formPanel);
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);
		formPanel.setAction("../simple/newProfile.html");

		final VerticalPanel formVerticalPanel = new VerticalPanel();
		formVerticalPanel.setStyleName("formPanel");
		formPanel.setWidget(formVerticalPanel);
		formVerticalPanel.setSize("100%", "100%");

		final FileUpload avatarUpload = new FileUpload();
		avatarUpload.setStyleName("formPanel");
		formVerticalPanel.add(avatarUpload);
		avatarUpload.setName(ProfileFormFields.AvatarField);
		final String mimeList = "image/png,image.jpg,image/jpeg";
		avatarUpload.getElement().setPropertyString("accept", mimeList);

		final Label lblNewLabel = new Label(messages.lblNewLabel_text());
		lblNewLabel.setStyleName("formLabel");
		formVerticalPanel.add(lblNewLabel);

		nameTextBox.setStyleName("formPanel");
		nameTextBox.setText(messages.textBox_text());
		formVerticalPanel.add(nameTextBox);
		nameTextBox.setWidth("100%");
		nameTextBox.setName(ProfileFormFields.NameField);

		final Label lblNewLabel_1 = new Label(messages.lblNewLabel_1_text());
		lblNewLabel_1.setStyleName("formLabel");
		formVerticalPanel.add(lblNewLabel_1);

		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setStyleName("formPanel");
		formVerticalPanel.add(horizontalPanel);

		final boolean female = Random.nextBoolean();

		maleRadioButton = new RadioButton(ProfileFormFields.genderGroupField,
				messages.maleRadioButton());
		maleRadioButton.setFormValue(ProfileFormFields.maleValue);
		maleRadioButton.setStyleName("formPanel");
		maleRadioButton.setValue(!female);
		horizontalPanel.add(maleRadioButton);

		femaleRadioButton = new RadioButton(ProfileFormFields.genderGroupField,
				messages.femaleRadioButton());
		femaleRadioButton.setFormValue(ProfileFormFields.femaleValue);
		femaleRadioButton.setStyleName("formPanel");
		femaleRadioButton.setValue(female);
		horizontalPanel.add(femaleRadioButton);

		final Label lblNewLabel_2 = new Label(messages.lblNewLabel_2_text());
		lblNewLabel_2.setStyleName("formLabel");
		formVerticalPanel.add(lblNewLabel_2);

		ageIntegerBox.setStyleName("formPanel");
		ageIntegerBox.setText(messages.integerBox_text());
		ageIntegerBox.setWidth("30%");
		formVerticalPanel.add(ageIntegerBox);
		ageIntegerBox.setName(ProfileFormFields.AgeField);

		final Label lblNewLabel_3 = new Label(messages.lblNewLabel_3_text());
		lblNewLabel_3.setStyleName("formLabel");
		formVerticalPanel.add(lblNewLabel_3);
		descriptionTextArea.setStyleName("formPanel");

		descriptionTextArea.setVisibleLines(6);
		descriptionTextArea.setText(messages.textArea_text());
		formVerticalPanel.add(descriptionTextArea);
		descriptionTextArea.setWidth("100%");
		descriptionTextArea.setName(ProfileFormFields.descriptionField);

		final HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
		horizontalPanel_1.setStyleName("formPanel");
		formVerticalPanel.add(horizontalPanel_1);
		formVerticalPanel.setCellHorizontalAlignment(horizontalPanel_1,
				HasHorizontalAlignment.ALIGN_CENTER);

		/**
		 * Submit Button for the form
		 */
		final Button button = new Button(messages.submitButton(), new ClickHandler() {

			/**
			 * Submit was performed.
			 */
			@Override
			public void onClick(final ClickEvent event) {
				formPanel.submit();
			}
		});
		horizontalPanel_1.add(button);
		horizontalPanel_1.setCellHorizontalAlignment(button, HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setCellHorizontalAlignment(button, HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setCellVerticalAlignment(button, HasVerticalAlignment.ALIGN_MIDDLE);

		/**
		 * Check the form content before transmission
		 */
		formPanel.addSubmitHandler(new FormPanel.SubmitHandler() {
			/**
			 * Check whether a correct name was set.
			 */
			@Override
			public void onSubmit(final SubmitEvent event) {
				if (nameTextBox.getText().length() == 0) {
					Window.alert(messages.nameLongerThanZero());
					event.cancel();
				}
			}
		});

		/**
		 * Handle the completed transmission of the form content
		 */
		formPanel.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(final SubmitCompleteEvent event) {
				readCurrentProfile();
				/**
				 * Just show current Profile for 2 seconds and than jump to
				 * Others Page
				 */
				final Timer timer = new Timer() {
					@Override
					public void run() {
						History.newItem(AppPart.othersLink.getTargetHistoryToken());
					}
				};
				timer.schedule(2000);
			}
		});
		readCurrentProfile();

	}

	/**
	 * reads the current profile for presetting all fields of the form
	 */
	private void readCurrentProfile() {

		/**
		 * CallBack Handler for the request for the profile of the user
		 */
		final AsyncCallback<Profile> callback = new AsyncCallback<Profile>() {
			/**
			 * When call failed present the user that there is a problem with
			 * his internet connection
			 */
			@Override
			public void onFailure(final Throwable caught) {
				final DialogBox dialogBox = new DialogBox();
				dialogBox.setText(messages.connectionError());
				dialogBox.setAnimationEnabled(true);
				final TextArea textArea = new TextArea();
				textArea.setVisibleLines(6);
				textArea.setText(messages.serverError());
				final Button closeButton = new Button(messages.closeButton());
				final VerticalPanel dialogVPanel = new VerticalPanel();
				dialogVPanel.add(textArea);
				dialogVPanel.add(closeButton);
				dialogBox.setWidget(dialogVPanel);
				dialogBox.center();

				closeButton.setFocus(true);

				/**
				 * Add a handler to close the DialogBox
				 */
				closeButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(final ClickEvent event) {
						dialogBox.hide();
					}
				});
			}

			/**
			 * request succeeded so copy all profile values into the
			 * corresponding fields
			 * 
			 * @param result
			 *            Profile of the user
			 */
			@Override
			public void onSuccess(final Profile result) {
				if (result.getAvatarUrl() != null) {
					image.setUrl(result.getAvatarUrl());
				}
				nameTextBox.setText(result.getName());
				descriptionTextArea.setText(result.getDescription());

				ageIntegerBox.setText(((Integer) result.getAge()).toString());
				if (Profile.male.equals(result.getGender())) {
					maleRadioButton.setValue(true);
					femaleRadioButton.setValue(false);
				} else {
					femaleRadioButton.setValue(true);
					maleRadioButton.setValue(false);
				}
			}
		};
		profileService.getProfile(callback);
	}

}
