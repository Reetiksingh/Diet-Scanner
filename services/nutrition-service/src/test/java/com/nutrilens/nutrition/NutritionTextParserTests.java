package com.nutrilens.nutrition;

import com.nutrilens.nutrition.application.NutritionTextParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NutritionTextParserTests {
    @Test
    void parsesLegacyNutritionLabelText() {
        var parsed = new NutritionTextParser().parse("Energy 120 kcal Protein 8g Total Sugar 4g Sodium 180mg Fat 2g");
        Assertions.assertEquals(120, parsed.calories().intValue());
        Assertions.assertEquals(8, parsed.proteinG().intValue());
        Assertions.assertEquals(4, parsed.sugarG().intValue());
        Assertions.assertEquals(180, parsed.sodiumMg().intValue());
    }
}

