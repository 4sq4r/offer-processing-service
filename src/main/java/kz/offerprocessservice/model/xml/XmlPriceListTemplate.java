package kz.offerprocessservice.model.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@XmlRootElement(name = "Template")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlPriceListTemplate {

    @XmlElementWrapper(name = "Headers")
    @XmlElement(name = "Header")
    private List<String> headers;
}
