package com.alliander.osgp.platform.cucumber.support.ws.microgrids.adhocmanagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.SetDataSystemIdentifier;
import com.alliander.osgp.platform.cucumber.steps.Keys;

public class SystemBuilder {

    protected Integer id;
    protected String type;

    protected List<SetDataSystemIdentifier> system = new ArrayList<>();

    public SystemBuilder() {
    }

    public SystemBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public SystemBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public SetDataSystemIdentifier build() {
        final SetDataSystemIdentifier system = new SetDataSystemIdentifier();
        system.setId(this.id);
        system.setType(this.type);
        return system;
    }

    public List<SetDataSystemIdentifier> buildList() {
        return this.system;
    }

    public SystemBuilder withSettings(final Map<String, String> settings) {
        for (int i = 1; i <= this.count(settings, Keys.KEY_SYSTEM_ID); i++) {
            this.system.add(this.withSettings(settings, i).build());
        }

        return this;
    }
    
    private SystemBuilder withSettings(final Map<String, String> settings, final int index) {
        if (this.hasKey(settings, Keys.KEY_SYSTEM_ID, index)) {
            this.withId(Integer.parseInt(getStringValue(settings, Keys.KEY_SYSTEM_ID, index)));
        } 
        if (this.hasKey(settings, Keys.KEY_SYSTEM_TYPE, index)) {
            this.withType(getStringValue(settings, Keys.KEY_SYSTEM_TYPE, index));
        } 
        return this;
    }
    
    private int count(final Map<String, String> settings, final String keyPrefix) {
        for (int i = 10; i > 0; i--) {
            if (this.hasKey(settings, keyPrefix, i)) {
                return i;
            }
        }
        return 0;
    }
    
    private boolean hasKey(final Map<String, String> settings, final String keyPrefix, final int index) {
        return settings.containsKey(makeKey(keyPrefix, index));
    }

    private String makeKey(final String keyPrefix, int index) {
        return keyPrefix + "_" + index;
    }

    private String getStringValue(final Map<String, String> settings, final String keyPrefix, final int index) {
        String key = makeKey(keyPrefix, index);
        return settings.get(key);
    }

}
