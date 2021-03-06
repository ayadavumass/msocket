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

package edu.umass.cs.msocket.proxy.console.commands;

import org.json.JSONArray;

import edu.umass.cs.gns.client.GnsProtocol;
import edu.umass.cs.gns.client.GnsProtocol.AccessType;
import edu.umass.cs.gns.client.GuidEntry;
import edu.umass.cs.gns.client.UniversalGnsClient;
import edu.umass.cs.gns.client.util.KeyPairUtils;
import edu.umass.cs.msocket.common.GnsConstants;
import edu.umass.cs.msocket.proxy.console.ConsoleModule;

/**
 * Command that creates the proxy group GUID
 * 
 * @author <a href="mailto:cecchet@cs.umass.edu">Emmanuel Cecchet </a>
 * @version 1.0
 */
public class ProxyGroupCreate extends ConsoleCommand
{

  /**
   * Creates a new <code>ProxyGroupCreate</code> object
   * 
   * @param module
   */
  public ProxyGroupCreate(ConsoleModule module)
  {
    super(module);
  }

  @Override
  public String getCommandDescription()
  {
    return "Create a new proxy group by creating a new GUID and registering it in the GNS.";
  }

  @Override
  public String getCommandName()
  {
    return "proxy_group_create";
  }

  @Override
  public String getCommandParameters()
  {
    return "ProxyGroupName";
  }

  /**
   * Override execute to not check for existing connectivity
   */
  public void execute(String commandText) throws Exception
  {
    parse(commandText);
  }

  @Override
  public void parse(String commandText) throws Exception
  {
    try
    {
      String proxyGroupName = commandText.trim();
      UniversalGnsClient gnsClient = module.getGnsClient();

      try
      {
        gnsClient.lookupGuid(proxyGroupName);
        console.printString("Group " + proxyGroupName + " already exists, use proxy_group_connect instead.\n");
        return;
      }
      catch (Exception expected)
      {
        // The group does not exists, that's good, let's create it
      }

      if (!module.isSilent())
        console.printString("Looking for proxy group " + proxyGroupName + " GUID and certificates...\n");
      GuidEntry myGuid = KeyPairUtils.getGuidEntryFromPreferences(module.getGnsClient().getGnsHost() + ":"
          + module.getGnsClient().getGnsPort(), proxyGroupName);

      if (myGuid == null)
      {
        if (!module.isSilent())
          console.printString("No keys found for proxy " + proxyGroupName + ". Generating new GUID and keys\n");
        myGuid = gnsClient.guidCreate(module.getAccountGuid(), proxyGroupName);
        // Allow anyone to access the group membership that contains verified
        // proxies
        gnsClient.groupAddMembershipReadPermission(myGuid, GnsProtocol.ALLUSERS);
      }

      // Create the fields containing the GUID lists
      createField(gnsClient, myGuid, GnsConstants.ACTIVE_PROXY_FIELD, false);
      createField(gnsClient, myGuid, GnsConstants.SUSPICIOUS_PROXY_FIELD, false);
      createField(gnsClient, myGuid, GnsConstants.INACTIVE_PROXY_FIELD, false);

      createField(gnsClient, myGuid, GnsConstants.ACTIVE_WATCHDOG_FIELD, false);
      createField(gnsClient, myGuid, GnsConstants.SUSPICIOUS_WATCHDOG_FIELD, false);
      createField(gnsClient, myGuid, GnsConstants.INACTIVE_WATCHDOG_FIELD, false);

      createField(gnsClient, myGuid, GnsConstants.ACTIVE_LOCATION_FIELD, false);
      // Open the field in READ to everyone so that mSocket clients can look it
      // up
      gnsClient.aclAdd(AccessType.READ_WHITELIST, myGuid, GnsConstants.ACTIVE_LOCATION_FIELD, null);
      createField(gnsClient, myGuid, GnsConstants.SUSPICIOUS_LOCATION_FIELD, false);
      createField(gnsClient, myGuid, GnsConstants.INACTIVE_PROXY_FIELD, false);

      if (!module.isSilent())
        console.printString("We are guid " + myGuid.getGuid() + "\n");
      module.setProxyGroupGuid(myGuid);
      module.setPromptString(ConsoleModule.CONSOLE_PROMPT + proxyGroupName + ">");
    }
    catch (Exception e)
    {
      e.printStackTrace();
      console.printString("Failed to create new proxy group ( " + e + ")\n");
    }
  }

  private void createField(UniversalGnsClient gnsClient, GuidEntry myGuid, String field, boolean writeAll)
      throws Exception
  {
    try
    {
      gnsClient.fieldRead(myGuid.getGuid(), field, myGuid);
    }
    catch (Exception fieldDoesNotExistLetsCreateIt)
    {
      gnsClient.fieldCreate(myGuid.getGuid(), field, new JSONArray(), myGuid);
    }
    gnsClient.aclAdd(GnsProtocol.AccessType.READ_WHITELIST, myGuid, field, GnsProtocol.ALLUSERS);
    if (writeAll)
      gnsClient.aclAdd(GnsProtocol.AccessType.WRITE_WHITELIST, myGuid, field, GnsProtocol.ALLUSERS);
  }
}
