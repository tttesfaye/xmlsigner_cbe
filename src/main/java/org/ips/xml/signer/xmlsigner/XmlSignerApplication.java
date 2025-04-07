package org.ips.xml.signer.xmlsigner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import javax.servlet.ServletException;

@SpringBootApplication
public class XmlSignerApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(XmlSignerApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(XmlSignerApplication.class, args);
    }
}

