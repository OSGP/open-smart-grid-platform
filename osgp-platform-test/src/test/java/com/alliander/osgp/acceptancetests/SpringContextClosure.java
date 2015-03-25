package com.alliander.osgp.acceptancetests;

public class SpringContextClosure {

    public SpringContextClosure() {
        ScopedGivWenZenForSlim.getContext().close();
    }
}
