/**
 *
 */
package bacond.timeslicer.svc.rolodex;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bacond.timeslicer.app.rolodex.ClientInfo;
import bacond.timeslicer.app.rolodex.IRolodex;

public class RolodexDomifier
{
	public void populateDoc(IRolodex rolodex, Document doc)
	{
		Element rootElem = doc.createElementNS("timeslice", "rolodex");
		rootElem.setPrefix("ts");

		doc.appendChild(rootElem);

		for (ClientInfo clientInfo: rolodex.getClientInfos())
		{
			Element infoElem = doc.createElementNS("timeslice", "client-info");
			infoElem.setAttribute("name", clientInfo.getName());
			infoElem.setPrefix("ts");
			rootElem.appendChild(infoElem);

			// TODO: should do proper linking.
		}
	}
}
