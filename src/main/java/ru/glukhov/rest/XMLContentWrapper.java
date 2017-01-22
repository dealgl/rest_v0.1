package ru.glukhov.rest;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by dgluhov on 22.01.2017.
 */

@Getter
@Setter
public class XMLContentWrapper implements XML {
    private String content;

    public XMLContentWrapper(String content){
        this.content = content;
    }
}
