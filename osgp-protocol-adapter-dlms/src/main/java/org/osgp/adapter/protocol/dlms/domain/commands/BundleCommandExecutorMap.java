package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;

@Component
public class BundleCommandExecutorMap {

    private final Map<Class<? extends ActionDto>, BundleCommandExecutor<? extends ActionDto, ? extends ActionResponseDto>> bundleCommandExecutor = new HashMap<>();

    public void addBundleCommandExector(final Class<? extends ActionDto> clazz,
            final BundleCommandExecutor<? extends ActionDto, ? extends ActionResponseDto> commandExecutor) {

        this.bundleCommandExecutor.put(clazz, commandExecutor);
    }

    @SuppressWarnings("unchecked")
    public BundleCommandExecutor<ActionDto, ActionResponseDto> getBundleCommandExecutor(
            final Class<? extends ActionDto> clazz) {
        return (BundleCommandExecutor<ActionDto, ActionResponseDto>) this.bundleCommandExecutor.get(clazz);
    }
}
