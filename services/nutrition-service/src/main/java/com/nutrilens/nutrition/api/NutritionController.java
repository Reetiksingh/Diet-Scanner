package com.nutrilens.nutrition.api;

import com.nutrilens.nutrition.api.dto.NutritionDtos.BarcodeScanRequest;
import com.nutrilens.nutrition.api.dto.NutritionDtos.FoodRequest;
import com.nutrilens.nutrition.api.dto.NutritionDtos.FoodResponse;
import com.nutrilens.nutrition.api.dto.NutritionDtos.LabelScanRequest;
import com.nutrilens.nutrition.api.dto.NutritionDtos.MealRequest;
import com.nutrilens.nutrition.api.dto.NutritionDtos.MealResponse;
import com.nutrilens.nutrition.api.dto.NutritionDtos.ScanResponse;
import com.nutrilens.nutrition.application.NutritionService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/nutrition")
public class NutritionController {
    private final NutritionService nutritionService;

    public NutritionController(NutritionService nutritionService) {
        this.nutritionService = nutritionService;
    }

    @GetMapping("/foods/search")
    public List<FoodResponse> search(@RequestParam(required = false) String q, @RequestParam(required = false) String barcode) {
        return nutritionService.search(q, barcode);
    }

    @PostMapping("/foods/custom")
    public FoodResponse createFood(@Valid @RequestBody FoodRequest request) {
        return nutritionService.createFood(request);
    }

    @GetMapping("/foods/{foodId}")
    public FoodResponse food(@PathVariable UUID foodId) {
        return nutritionService.getFood(foodId);
    }

    @PostMapping("/meals")
    public MealResponse logMeal(@Valid @RequestBody MealRequest request) {
        return nutritionService.logMeal(request);
    }

    @PostMapping("/scans/barcode")
    public ScanResponse scanBarcode(@Valid @RequestBody BarcodeScanRequest request) {
        return nutritionService.scanBarcode(request);
    }

    @PostMapping("/scans/label")
    public ScanResponse scanLabel(@Valid @RequestBody LabelScanRequest request) {
        return nutritionService.scanLabel(request);
    }
}

