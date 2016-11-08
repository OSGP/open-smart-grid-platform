package com.alliander.osgp.platform.cucumber.steps.database;

import static com.alliander.osgp.platform.cucumber.core.Helpers.cleanRepoAbstractEntity;
import static com.alliander.osgp.platform.cucumber.core.Helpers.cleanRepoSerializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Manufacturer;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.ManufacturerRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.domain.core.valueobjects.PlatformDomain;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;
import com.alliander.osgp.platform.cucumber.steps.Defaults;

@Component
public class DatabaseSteps {

    @Autowired
    private OrganisationRepository organizationRepo;

    @Autowired
    private ManufacturerRepository manufacturerRepo;

    @Autowired
    private DeviceModelRepository deviceModelRepo;

    @Autowired
    private DeviceRepository deviceRepo;

    @Autowired
    private SmartMeterRepository smartMeterRepo;

    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepo;

    @Autowired
    private OslpDeviceRepository oslpDeviceRepo;
    
    @Autowired
    private SsldRepository ssldRepository;
    
    /**
     * This method is used to create default data not directly related to the
     * specific tests. For example: The test-org organization which is used to
     * send authorized requests to the platform.
     */
    private void insertDefaultData() {
        // TODO: Better would be to have some sort of init method in the
        // steps.database package which will create the necessary basic
        // entities.

        if (this.organizationRepo.findByOrganisationIdentification(Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION) == null) {
            // Create test organization used within the tests.
            final Organisation testOrg = new Organisation(Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION,
                    Defaults.DEFAULT_ORGANIZATION_DESCRIPTION, Defaults.DEFAULT_PREFIX, PlatformFunctionGroup.ADMIN);
            testOrg.addDomain(PlatformDomain.COMMON);
            testOrg.addDomain(PlatformDomain.PUBLIC_LIGHTING);
            testOrg.addDomain(PlatformDomain.TARIFF_SWITCHING);
            testOrg.setIsEnabled(true);

            this.organizationRepo.save(testOrg);
        }

        // Create default test manufacturer
        final Manufacturer manufacturer = new Manufacturer(Defaults.DEFAULT_MANUFACTURER_ID,
                Defaults.DEFAULT_MANUFACTURER_NAME, false);
        this.manufacturerRepo.save(manufacturer);

        // Create the default test model
        final DeviceModel deviceModel = new DeviceModel(manufacturer, Defaults.DEFAULT_DEVICE_MODEL_MODEL_CODE,
                Defaults.DEFAULT_DEVICE_MODEL_DESCRIPTION, true);
        this.deviceModelRepo.save(deviceModel);
    }

    public void prepareDatabaseForScenario() {
        cleanRepoAbstractEntity(this.oslpDeviceRepo);
        cleanRepoAbstractEntity(this.deviceAuthorizationRepo);
        cleanRepoSerializable(this.smartMeterRepo);
        cleanRepoSerializable(this.deviceRepo);
        cleanRepoSerializable(this.deviceModelRepo);
        cleanRepoSerializable(this.manufacturerRepo);
        cleanRepoSerializable(this.ssldRepository);
        cleanRepoSerializable(this.organizationRepo);

        insertDefaultData();
    }
}
