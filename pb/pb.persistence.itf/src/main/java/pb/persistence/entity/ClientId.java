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
 * @since 25.10.2013
 * @version 1.0
 * @author oliver
 */
package pb.persistence.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The id of a candidate is simply evaluated by using it's ip address. This
 * means this only works in an area where no proxies are active and the network
 * garantees that the client keeps it's ip until the party is over.
 * 
 * @author oliver
 */
@Entity
@Table(name = ClientId.TABLE_NAME)
public class ClientId {
  public static final String TABLE_NAME = "CLIENT_ID";

  public static final String COLUMN_CANDIDATE_ID = "CANDIDATE_ID";
  public static final String COLUMN_IP = "IP";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = COLUMN_CANDIDATE_ID)
  protected int id;

  @Basic
  @Column(name = COLUMN_IP)
  protected String ip;

  public ClientId() {
    super();
  }

  public ClientId(final String ip) {
    super();
    this.ip = ip;
  }

  /**
   * @return the id
   */
  public int getId() {
    return this.id;
  }

  /**
   * @param id the id to set
   */
  public void setId(final int id) {
    this.id = id;
  }

  /**
   * @return the ip
   */
  public String getIp() {
    return this.ip;
  }

  /**
   * @param ip the ip to set
   */
  public void setIp(final String ip) {
    this.ip = ip;
  }
}
