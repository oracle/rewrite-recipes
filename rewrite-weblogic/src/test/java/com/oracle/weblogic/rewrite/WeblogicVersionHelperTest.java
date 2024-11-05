/*
 * Copyright (c) 2024 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 *
 */
package com.oracle.weblogic.rewrite;

import org.junit.jupiter.api.Test;
import org.openrewrite.InMemoryExecutionContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeblogicVersionHelperTest {

    @Test
    void testGetNewerVersion() {
        InMemoryExecutionContext ctx = new InMemoryExecutionContext();
        String currentVersion = "12.2.1-4-0";
        String versionPattern = "14.1.1-0-0";

        Optional<String> result = WeblogicVersionHelper.getNewerVersion(versionPattern, currentVersion, ctx);
        assertEquals("14.1.1-0-0", result.orElse("No newer version found"));
    }
}
