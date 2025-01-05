package kz.offerprocessservice.model.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlOffer {

    @XmlElement(name = "OfferCode")
    private String offerCode;
    @XmlElement(name = "OfferName")
    private String offerName;
    @XmlElementWrapper(name = "stocks")
    @XmlElement(name = "stock")
    private List<XmlStock> stocks;
}
