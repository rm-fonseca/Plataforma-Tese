//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.12.19 at 06:14:14 PM GMT 
//


package plataforma.modelointerno;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="latitudeFrom" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="latitudeTo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="longitudeFrom" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="longitudeTo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="repositories" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ignoreExtraProperties" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="disableRelation" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="disableCombine" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "latitudeFrom",
    "latitudeTo",
    "longitudeFrom",
    "longitudeTo",
    "repositories",
    "ignoreExtraProperties",
    "disableRelation",
    "disableCombine"
})
@XmlRootElement(name = "searchByBoxRequest")
public class SearchByBoxRequest {

    protected int latitudeFrom;
    protected int latitudeTo;
    protected int longitudeFrom;
    protected int longitudeTo;
    @XmlElement(type = Integer.class)
    protected List<Integer> repositories;
    @XmlElement(defaultValue = "false")
    protected boolean ignoreExtraProperties;
    @XmlElement(defaultValue = "false")
    protected boolean disableRelation;
    @XmlElement(defaultValue = "false")
    protected boolean disableCombine;

    /**
     * Gets the value of the latitudeFrom property.
     * 
     */
    public int getLatitudeFrom() {
        return latitudeFrom;
    }

    /**
     * Sets the value of the latitudeFrom property.
     * 
     */
    public void setLatitudeFrom(int value) {
        this.latitudeFrom = value;
    }

    /**
     * Gets the value of the latitudeTo property.
     * 
     */
    public int getLatitudeTo() {
        return latitudeTo;
    }

    /**
     * Sets the value of the latitudeTo property.
     * 
     */
    public void setLatitudeTo(int value) {
        this.latitudeTo = value;
    }

    /**
     * Gets the value of the longitudeFrom property.
     * 
     */
    public int getLongitudeFrom() {
        return longitudeFrom;
    }

    /**
     * Sets the value of the longitudeFrom property.
     * 
     */
    public void setLongitudeFrom(int value) {
        this.longitudeFrom = value;
    }

    /**
     * Gets the value of the longitudeTo property.
     * 
     */
    public int getLongitudeTo() {
        return longitudeTo;
    }

    /**
     * Sets the value of the longitudeTo property.
     * 
     */
    public void setLongitudeTo(int value) {
        this.longitudeTo = value;
    }

    /**
     * Gets the value of the repositories property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the repositories property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRepositories().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getRepositories() {
        if (repositories == null) {
            repositories = new ArrayList<Integer>();
        }
        return this.repositories;
    }

    /**
     * Gets the value of the ignoreExtraProperties property.
     * 
     */
    public boolean isIgnoreExtraProperties() {
        return ignoreExtraProperties;
    }

    /**
     * Sets the value of the ignoreExtraProperties property.
     * 
     */
    public void setIgnoreExtraProperties(boolean value) {
        this.ignoreExtraProperties = value;
    }

    /**
     * Gets the value of the disableRelation property.
     * 
     */
    public boolean isDisableRelation() {
        return disableRelation;
    }

    /**
     * Sets the value of the disableRelation property.
     * 
     */
    public void setDisableRelation(boolean value) {
        this.disableRelation = value;
    }

    /**
     * Gets the value of the disableCombine property.
     * 
     */
    public boolean isDisableCombine() {
        return disableCombine;
    }

    /**
     * Sets the value of the disableCombine property.
     * 
     */
    public void setDisableCombine(boolean value) {
        this.disableCombine = value;
    }

}
