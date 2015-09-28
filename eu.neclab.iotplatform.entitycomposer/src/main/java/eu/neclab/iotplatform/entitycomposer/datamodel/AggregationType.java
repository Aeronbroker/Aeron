package eu.neclab.iotplatform.entitycomposer.datamodel;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "AggregationType")
@XmlEnum
public enum AggregationType{
	
	SUM, /*sum*/
	
	AVG, /*average*/
	
	MAX, /*maximum*/
	
	MIN, /*minimum*/

}
