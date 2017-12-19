package org.geowebcache.config;

import org.geowebcache.GeoWebCacheException;
import org.geowebcache.filter.parameters.StringParameterFilter;
import org.geowebcache.grid.*;
import org.geowebcache.layer.wms.WMSLayer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GWCConfigIntegrationTestData {

    // Names / ids for config objects
    public static final String LAYER_TOPP_STATES = "topp:states";

    public static final String GRIDSET_EPSG4326 = "EPSG:4326";
    public static final String GRIDSET_EPSG2163 = "EPSG:2163";

    public static final String BLOBSTORE_FILE_DEFAULT = "defaultCache";

    public static void setUpTestData(GWCConfigIntegrationTestSupport testSupport) throws GeoWebCacheException, IOException {
        //set up service information
        ServerConfiguration serverConfiguration = testSupport.getServerConfiguration();
        serverConfiguration.getServiceInformation().setTitle("GeoWebCache");

        //Set up layers & gridsets
        //TODO: Update to use the new api
        GridSetConfiguration gridSetConfiguration = testSupport.getGridSetConfiguration();

        GridSet epsg2163 = GridSetFactory.createGridSet(GRIDSET_EPSG2163, SRS.getSRS(GRIDSET_EPSG2163),
                new BoundingBox(-2495667.977678598, -2223677.196231552,
                                3291070.6104286816, 959189.3312465074),
                false, null,
                new double[]{ 25000000, 1000000, 100000, 25000 },
                null, GridSetFactory.DEFAULT_PIXEL_SIZE_METER,
                null, 200, 200, false);

        gridSetConfiguration.addGridSet(epsg2163);

        TileLayerConfiguration tileLayerConfiguration = testSupport.getTileLayerConfigurations().get(0);

        testSupport.getGridSetBroker().put(epsg2163);
        tileLayerConfiguration.initialize(testSupport.getGridSetBroker());

        Map<String, GridSubset > subSets = new HashMap<>();
        subSets.put(GRIDSET_EPSG4326, GridSubsetFactory.createGridSubSet(testSupport.getGridSetBroker().WORLD_EPSG4326, new BoundingBox(-129.6, 3.45,-62.1,70.9), null, null));
        subSets.put(GRIDSET_EPSG2163, GridSubsetFactory.createGridSubSet(epsg2163));

        StringParameterFilter parameterFilter = new StringParameterFilter();
        parameterFilter.setKey("STYLES");
        parameterFilter.setDefaultValue("population");
        parameterFilter.setValues(Arrays.asList("population", "polygon", "pophatch"));

        tileLayerConfiguration.addLayer(new WMSLayer(
                LAYER_TOPP_STATES, new String[]{"http://demo.opengeo.org/geoserver/topp/wms"}, null, null,
                Arrays.asList("image/gif", "image/jpeg", "image/png", "image/png8"),  subSets,
                Collections.singletonList(parameterFilter), null, null, true));

        //TODO: update to use new API

        tileLayerConfiguration.save();

        //Set up blobstores
        BlobStoreConfigurationCatalog blobStoreConfiguration = testSupport.getBlobStoreConfiguration();

        FileBlobStoreConfig blobStore = new FileBlobStoreConfig(BLOBSTORE_FILE_DEFAULT);
        blobStore.setEnabled(false);
        blobStore.setBaseDirectory("/tmp/defaultCache");
        blobStore.setFileSystemBlockSize(4096);

        //TODO: Use new API to add blobstore
        blobStoreConfiguration.getBlobStores().add(blobStore);
        blobStoreConfiguration.save();
    }
}
