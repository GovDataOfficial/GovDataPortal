/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS
 *
 * This file is part of Open Data Platform.
 *
 * Open Data Platform is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Open Data Plaform is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with Open Data Platform.  If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

package de.fhg.fokus.odp.rssservlet.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;

// TODO: Auto-generated Javadoc
/**
 * The Class AbderaSupport.
 */
@Provider
@Produces(MediaType.APPLICATION_ATOM_XML)
@Consumes(MediaType.APPLICATION_ATOM_XML)
public class AbderaSupport implements MessageBodyWriter<Object>, MessageBodyReader<Object> {

    /** The Constant abdera. */
    private final static Abdera abdera = new Abdera();

    /**
     * Gets the abdera.
     * 
     * @return the abdera
     */
    public static Abdera getAbdera() {
        return abdera;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.ws.rs.ext.MessageBodyReader#isReadable(java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
     */
    @Override
    public boolean isReadable(Class<?> type, Type arg1, Annotation[] arg2, MediaType arg3) {
        return (Feed.class.isAssignableFrom(type) || Entry.class.isAssignableFrom(type));
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.ws.rs.ext.MessageBodyReader#readFrom(java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType,
     * javax.ws.rs.core.MultivaluedMap, java.io.InputStream)
     */
    @Override
    public Object readFrom(Class<Object> feedOrEntry, Type arg1, Annotation[] arg2, MediaType arg3, MultivaluedMap<String, String> arg4,
            InputStream inputStream) throws IOException, WebApplicationException {
        Document<Element> doc = getAbdera().getParser().parse(inputStream);
        Element el = doc.getRoot();
        if (feedOrEntry.isAssignableFrom(el.getClass())) {
            return el;
        } else {
            throw new IOException("Unexpected payload, expected " + feedOrEntry.getName() + ", received " + el.getClass().getName());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.ws.rs.ext.MessageBodyWriter#getSize(java.lang.Object, java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[],
     * javax.ws.rs.core.MediaType)
     */
    @Override
    public long getSize(Object arg0, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4) {
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.ws.rs.ext.MessageBodyWriter#isWriteable(java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[],
     * javax.ws.rs.core.MediaType)
     */
    @Override
    public boolean isWriteable(Class<?> type, Type arg1, Annotation[] arg2, MediaType arg3) {
        return (Feed.class.isAssignableFrom(type) || Entry.class.isAssignableFrom(type));
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.ws.rs.ext.MessageBodyWriter#writeTo(java.lang.Object, java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[],
     * javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap, java.io.OutputStream)
     */
    @Override
    public void writeTo(Object feedOrEntry, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType mediaType,
            MultivaluedMap<String, Object> headers, OutputStream outputStream) throws IOException, WebApplicationException {
        if (feedOrEntry instanceof Feed) {
            Feed feed = (Feed) feedOrEntry;
            Document<Feed> doc = feed.getDocument();
            doc.writeTo(outputStream);
        } else {
            Entry entry = (Entry) feedOrEntry;
            Document<Entry> doc = entry.getDocument();
            doc.writeTo(outputStream);
        }

    }

}
