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

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;


import edu.umass.cs.gns.client.GuidEntry;
import edu.umass.cs.gns.client.UniversalGnsClient;
import edu.umass.cs.gns.client.util.KeyPairUtils;
import edu.umass.cs.gns.exceptions.GnsException;

/**
 * This class defines a GnsCredentials that contains a valid GuidEntry (GUID,
 * optional alias as well as private and public keys) and a connection to the
 * GNS instance where these credentials are valid.
 * 
 * @author <a href="mailto:cecchet@cs.umass.edu">Emmanuel Cecchet</a>
 * @version 1.0
 */
public class GnsCredentials
{
  private String             gnsHost;
  private int                gnsPort;
  private String             guidAlias;
  private UniversalGnsClient gnsClient;
  private GuidEntry          guidEntry;
  private Logger             logger = Logger.getLogger("GnsSocket");

  /**
   * Creates a new <code>GnsCredentials</code> object
   * 
   * @param gnsHost hostname of a GNS instance
   * @param gnsPort port number where the GNS is running on hostname
   * @param guidAlias alias or entity name to lookup to retrieve the GUID
   * @throws Exception if a GNS error occurs
   */
  public GnsCredentials(String gnsHost, int gnsPort, String guidAlias) throws Exception
  {
    logger.setLevel(Level.WARNING);

    this.gnsHost = gnsHost;
    this.gnsPort = gnsPort;
    this.guidAlias = guidAlias;

    gnsClient = new UniversalGnsClient(gnsHost, gnsPort);
    gnsClient.checkConnectivity();
    // Set the global variable for other activities
    UniversalGnsClient.setGns(gnsClient);

    if (guidAlias == null)
    {
      logger.info("No alias provided, GUID-less only queries can be performed with these credentials");
      return;
    }

    logger.info("Looking for user " + guidAlias + " GUID and certificates...");
    guidEntry = KeyPairUtils.getGuidEntryFromPreferences(gnsHost + ":" + gnsPort, guidAlias);

    if (guidEntry == null)
      throw new GnsException("GNS credentials not valid");
  }

  /**
   * Creates a new <code>GnsCredentials</code> object from an existing client
   * and account GUID
   * 
   * @param gnsClient
   * @param guidEntry
   */
  public GnsCredentials(UniversalGnsClient gnsClient, GuidEntry guidEntry)
  {
    this.gnsClient = gnsClient;
    this.guidEntry = guidEntry;
    this.gnsHost = gnsClient.getGnsHost();
    this.gnsPort = gnsClient.getGnsPort();
  }

  /**
   * Returns the gnsHost value.
   * 
   * @return Returns the gnsHost.
   */
  public String getGnsHost()
  {
    return gnsHost;
  }

  /**
   * Returns the gnsPort value.
   * 
   * @return Returns the gnsPort.
   */
  public int getGnsPort()
  {
    return gnsPort;
  }

  /**
   * Returns the guidAlias value.
   * 
   * @return Returns the guidAlias.
   */
  public String getGuidAlias()
  {
    return guidAlias;
  }

  /**
   * Returns the gnsClient value.
   * 
   * @return Returns the gnsClient.
   */
  public UniversalGnsClient getGnsClient()
  {
    return gnsClient;
  }

  /**
   * Returns the guidEntry value.
   * 
   * @return Returns the guidEntry.
   */
  public GuidEntry getGuidEntry()
  {
    return guidEntry;
  }

  /**
   * Sets the guidEntry value.
   * 
   * @param guidEntry The guidEntry to set.
   */
  public void setGuidEntry(GuidEntry guidEntry)
  {
    this.guidEntry = guidEntry;
  }

  /**
   * Return default credentials to the default gns.name GNS using the default
   * account GUID set with the GNS console on this machine.
   * 
   * @return default credentials
   * @throws Exception if the default GUID has not been set or a GNS access
   *           error occurs
   */
  public static GnsCredentials getDefaultCredentials() throws IOException
  {
	  try {
	    String defaultGns = KeyPairUtils.getDefaultGnsFromPreferences();
	    if (defaultGns == null)
	      throw new GnsException("No default GNS configured on this machine. Use the GNS console to define one.");
	    
	    GuidEntry guidAlias = KeyPairUtils.getDefaultGuidEntryFromPreferences(defaultGns);
	    if (guidAlias == null)
	    {
	    	//throw new GnsException("No default GUID configured on this machine for default GNS " + defaultGns
	    	//		+ ". Use the GNS console to define one.");
	    	
	    	// Split GNS host port (uses a space and not a : as it is meant to be used
		    // as is by the gns_connect console command
		    StringTokenizer st = new StringTokenizer(defaultGns, ":");
		    return new GnsCredentials(st.nextToken(), Integer.valueOf(st.nextToken()), null);
	    } 
	    else
	    {
	    	// Split GNS host port (uses a space and not a : as it is meant to be used
		    // as is by the gns_connect console command
		    StringTokenizer st = new StringTokenizer(defaultGns, ":");
		    return new GnsCredentials(st.nextToken(), Integer.valueOf(st.nextToken()), guidAlias.getEntityName());
	    }
	  } catch(Exception ex)
	  {
		  throw new IOException(ex);
	  }
  }
  
}