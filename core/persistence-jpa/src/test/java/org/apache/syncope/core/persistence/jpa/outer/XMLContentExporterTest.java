/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.core.persistence.jpa.outer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.syncope.core.persistence.api.content.ContentExporter;
import org.apache.syncope.core.persistence.jpa.AbstractTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional("Master")
public class XMLContentExporterTest extends AbstractTest {

    @Autowired
    private ContentExporter exporter;

    /**
     * Also checks for SYNCOPE-1307.
     *
     * @throws Exception
     */
    @Test
    public void issueSYNCOPE1128() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        exporter.export("Master", baos, null, null, null);

        String exported = baos.toString(Charset.defaultCharset());
        assertTrue(StringUtils.isNotBlank(exported));

        List<String> realms = IOUtils.readLines(
                IOUtils.toInputStream(exported, Charset.defaultCharset()), Charset.defaultCharset()).stream().
                filter(row -> StringUtils.startsWithIgnoreCase(row.trim(), "<REALM")).collect(Collectors.toList());
        assertEquals(4, realms.size());
        assertTrue(StringUtils.containsIgnoreCase(realms.get(0), "NAME=\"/\""));
        assertTrue(StringUtils.containsIgnoreCase(realms.get(1), "NAME=\"odd\""));
        assertTrue(StringUtils.containsIgnoreCase(realms.get(2), "NAME=\"even\""));
        assertTrue(StringUtils.containsIgnoreCase(realms.get(3), "NAME=\"two\""));
    }
}
