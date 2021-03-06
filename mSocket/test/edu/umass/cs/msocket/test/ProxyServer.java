/**
 * Mobility First - Global Name Resolution Service (GNS)
 * Copyright (C) 2013 University of Massachusetts - Emmanuel Cecchet.
 * Contact: cecchet@cs.umass.edu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 * Initial developer(s): Emmanuel Cecchet.
 * Contributor(s): ______________________.
 */

package edu.umass.cs.msocket.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedList;

import edu.umass.cs.msocket.MServerSocket;
import edu.umass.cs.msocket.MSocket;
import edu.umass.cs.msocket.common.policies.FixedProxyPolicy;

/**
 * To test the proxy server functions
 * 
 * @author <a href="mailto:cecchet@cs.umass.edu">Emmanuel Cecchet</a>
 * @version 1.0
 */
public class ProxyServer 
{
	public static void main(String[] args) throws IOException
	{
		LinkedList<InetSocketAddress> proxyList = new LinkedList<InetSocketAddress>();
	    proxyList.add(new InetSocketAddress("planetlab1.inf.ethz.ch", 11989));
		MServerSocket mserverSoc = new MServerSocket("testMServer", new FixedProxyPolicy(proxyList));
		
		System.out.println("opened a server socket behind proxy");
		
		MSocket msocket = mserverSoc.accept();
		
		System.out.println("accepted a connection");
	}
}