/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.persistence.influxdb.internal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Map;

import org.eclipse.jdt.annotation.DefaultLocation;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openhab.core.items.Metadata;
import org.openhab.core.items.MetadataKey;
import org.openhab.core.items.MetadataRegistry;
import org.openhab.core.library.items.NumberItem;
import org.openhab.persistence.influxdb.InfluxDBPersistenceService;

/**
 * @author Joan Pujol Espinar - Initial contribution
 */
@SuppressWarnings("null") // In case of any NPE it will cause test fail that it's the expected result
@NonNullByDefault(value = { DefaultLocation.PARAMETER, DefaultLocation.RETURN_TYPE })
public class ItemToStorePointCreatorTest {
    @Mock
    private InfluxDBConfiguration influxDBConfiguration;
    @Mock
    private MetadataRegistry metadataRegistry;
    private ItemToStorePointCreator instance;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        when(influxDBConfiguration.isAddCategoryTag()).thenReturn(false);
        when(influxDBConfiguration.isAddLabelTag()).thenReturn(false);
        when(influxDBConfiguration.isAddTypeTag()).thenReturn(false);
        when(influxDBConfiguration.isReplaceUnderscore()).thenReturn(false);

        instance = new ItemToStorePointCreator(influxDBConfiguration, metadataRegistry);
    }

    @After
    public void after() {
        instance = null;
        influxDBConfiguration = null;
        metadataRegistry = null;
    }

    @Test
    public void convertBasicItem() {
        NumberItem item = ItemTestHelper.createNumberItem("myitem", 5);
        InfluxPoint point = instance.convert(item, null);

        assertThat(point.getMeasurementName(), equalTo(item.getName()));
        assertThat("Must Store item name", point.getTags(), hasEntry("item", item.getName()));
        assertThat(point.getValue(), equalTo(new BigInteger("5")));
    }

    @Test
    public void shouldUseAliasAsMeasurementNameIfProvided() {
        NumberItem item = ItemTestHelper.createNumberItem("myitem", 5);
        InfluxPoint point = instance.convert(item, "aliasName");
        assertThat(point.getMeasurementName(), is("aliasName"));
    }

    @Test
    public void shouldStoreCategoryTagIfProvidedAndConfigured() {
        NumberItem item = ItemTestHelper.createNumberItem("myitem", 5);
        item.setCategory("categoryValue");

        when(influxDBConfiguration.isAddCategoryTag()).thenReturn(true);
        InfluxPoint point = instance.convert(item, null);
        assertThat(point.getTags(), hasEntry(InfluxDBConstants.TAG_CATEGORY_NAME, "categoryValue"));

        when(influxDBConfiguration.isAddCategoryTag()).thenReturn(false);
        point = instance.convert(item, null);
        assertThat(point.getTags(), not(hasKey(InfluxDBConstants.TAG_CATEGORY_NAME)));
    }

    @Test
    public void shouldStoreTypeTagIfProvidedAndConfigured() {
        NumberItem item = ItemTestHelper.createNumberItem("myitem", 5);

        when(influxDBConfiguration.isAddTypeTag()).thenReturn(true);
        InfluxPoint point = instance.convert(item, null);
        assertThat(point.getTags(), hasEntry(InfluxDBConstants.TAG_TYPE_NAME, "Number"));

        when(influxDBConfiguration.isAddTypeTag()).thenReturn(false);
        point = instance.convert(item, null);
        assertThat(point.getTags(), not(hasKey(InfluxDBConstants.TAG_TYPE_NAME)));
    }

    @Test
    public void shouldStoreTypeLabelIfProvidedAndConfigured() {
        NumberItem item = ItemTestHelper.createNumberItem("myitem", 5);
        item.setLabel("ItemLabel");

        when(influxDBConfiguration.isAddLabelTag()).thenReturn(true);
        InfluxPoint point = instance.convert(item, null);
        assertThat(point.getTags(), hasEntry(InfluxDBConstants.TAG_LABEL_NAME, "ItemLabel"));

        when(influxDBConfiguration.isAddLabelTag()).thenReturn(false);
        point = instance.convert(item, null);
        assertThat(point.getTags(), not(hasKey(InfluxDBConstants.TAG_LABEL_NAME)));
    }

    @Test
    public void shouldStoreMetadataAsTagsIfProvided() {
        NumberItem item = ItemTestHelper.createNumberItem("myitem", 5);
        MetadataKey metadataKey = new MetadataKey(InfluxDBPersistenceService.SERVICE_NAME, item.getName());

        when(metadataRegistry.get(metadataKey))
                .thenReturn(new Metadata(metadataKey, "", Map.of("key1", "val1", "key2", "val2")));

        InfluxPoint point = instance.convert(item, null);
        assertThat(point.getTags(), hasEntry("key1", "val1"));
        assertThat(point.getTags(), hasEntry("key2", "val2"));
    }
}
