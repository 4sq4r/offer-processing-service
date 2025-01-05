package kz.offerprocessservice.model.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement(name = "stocks")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlStock {

    @XmlElement
    private String warehouseName;
    @XmlElement
    private Integer stock;
}
