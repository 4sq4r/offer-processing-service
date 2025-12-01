package kz.offerprocessservice.model.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@XmlRootElement(name = "offers")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlPriceListTemplate {

    @XmlElement(name = "offer")
    private List<XmlOffer> offers = new ArrayList<>();
}
