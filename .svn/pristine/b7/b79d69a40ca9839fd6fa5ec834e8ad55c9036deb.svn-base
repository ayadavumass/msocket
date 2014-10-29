/*******************************************************************************
 *
 * Mobility First - mSocket library
 * Copyright (C) 2013, 2014 - University of Massachusetts Amherst
 * Contact: arun@cs.umass.edu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 * Initial developer(s): Arun Venkataramani, Aditya Yadav, Emmanuel Cecchet.
 * Contributor(s): ______________________.
 *
 *******************************************************************************/

package edu.umass.cs.msocket.gns;

import java.net.SocketAddress;
import java.util.LinkedList;

import org.json.JSONArray;

import edu.umass.cs.gns.client.GuidEntry;
import edu.umass.cs.gns.client.UniversalGnsClient;

/**
 * This class defines a GnsSocketAddress
 * 
 * @author <a href="mailto:cecchet@cs.umass.edu">Emmanuel Cecchet</a>
 * @version 1.0
 */
public class GnsSocketAddress extends SocketAddress
{
  private static final long  serialVersionUID = -5992203106444616039L;

  private UniversalGnsClient gnsClient;
  private GuidEntry          myGuid;
  private String             ip;
  private int                port;
  private boolean            isProxy;

  /**
   * Creates a new <code>InetGnsAddress</code> object
   * 
   * @param client the client library to use to access the GNS
   * @param myGuid the GUID to use to access the GNS
   */
  public GnsSocketAddress(UniversalGnsClient client, GuidEntry myGuid)
  {
    if (client == null)
      throw new NullPointerException("Invalid null GNS client in InetGnsAddress");
    this.gnsClient = client;
    this.myGuid = myGuid;
  }

  /**
   * Get all IP address/port number for the given service belonging to the guid.
   * 
   * @param guid guid to lookup
   * @param serviceName service to access
   * @return array of InetSocketAddres or null if no address was found
   * @throws Exception if a GNS error occurs
   */
  public GnsSocketAddress[] getAllByGuid(String guid, String serviceName) throws Exception
  {
    JSONArray addresses = gnsClient.fieldRead(guid, serviceName, myGuid);

    if (addresses.length() == 0)
      return null;

    LinkedList<GnsSocketAddress> addrList = new LinkedList<GnsSocketAddress>();

    for (int i = 0; i < addresses.length(); i++)
    {
      GnsSocketAddress sock = new GnsSocketAddress(gnsClient, myGuid);
      String ipPort = addresses.getString(i);
      if (ipPort.startsWith("P"))
      {
        sock.setProxy(true);
        ipPort = ipPort.substring(1);
      }
      else
        sock.setProxy(false);
      int colon = ipPort.indexOf(':');
      if (colon == -1)
        throw new Exception("Invalid IP:Port value " + ipPort + " for service " + serviceName);

      sock.setIp(ipPort.substring(0, colon));
      sock.setPort(Integer.valueOf(ipPort.substring(colon + 1)));
      addrList.add(sock);
    }

    return addrList.toArray(new GnsSocketAddress[addrList.size()]);
  }

  /**
   * Get the first IP address/port number for the given service belonging to the
   * guid.
   * 
   * @param guid guid to lookup
   * @param serviceName service to access
   * @return an InetSocketAddres
   * @throws Exception if a GNS error occurs
   */
  public GnsSocketAddress getByGuid(String guid, String serviceName) throws Exception
  {
    final GnsSocketAddress[] addrs = getAllByGuid(guid, serviceName);
    if (addrs == null)
      return null;
    else
      return addrs[0];
  }

  /**
   * Get all IP address/port number for the given service belonging to the
   * entity/alias.
   * 
   * @param serviceName service to access
   * @return array of InetSocketAddres if a GNS error occurs
   * @throws Exception if a GNS error occurs
   */
  public GnsSocketAddress[] getAllByEntityName(String serviceName) throws Exception
  {
    String guid = gnsClient.lookupGuid(serviceName);
    return getAllByGuid(guid, serviceName);

  }

  /**
   * Get the first IP address/port number for the given service belonging to the
   * entity/alias.
   * 
   * @param serviceName service to access
   * @return an InetSocketAddres
   * @throws Exception if a GNS error occurs
   */
  public GnsSocketAddress getByEntityName(String serviceName) throws Exception
  {
    final GnsSocketAddress[] addrs = getAllByEntityName(serviceName);
    if (addrs == null || addrs.length == 0)
      return null;
    else
      return addrs[0];
  }

  /**
   * Returns the ip value.
   * 
   * @return Returns the ip.
   */
  public String getIp()
  {
    return ip;
  }

  /**
   * Sets the ip value.
   * 
   * @param ip The ip to set.
   */
  public void setIp(String ip)
  {
    this.ip = ip;
  }

  /**
   * Returns the port value.
   * 
   * @return Returns the port.
   */
  public int getPort()
  {
    return port;
  }

  /**
   * Sets the port value.
   * 
   * @param port The port to set.
   */
  public void setPort(int port)
  {
    this.port = port;
  }

  /**
   * Returns the isProxy value.
   * 
   * @return Returns the isProxy.
   */
  public boolean isProxy()
  {
    return isProxy;
  }

  /**
   * Sets the isProxy value.
   * 
   * @param isProxy The isProxy to set.
   */
  public void setProxy(boolean isProxy)
  {
    this.isProxy = isProxy;
  }

}
