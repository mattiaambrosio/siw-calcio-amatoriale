package it.uniroma3.siw.calcio_amatoriale.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.uniroma3.siw.calcio_amatoriale.model.StrategiaResult;
import it.uniroma3.siw.calcio_amatoriale.service.PerformanceService;

/**
 * Controller Admin per l'analisi sperimentale delle prestazioni JPA (§8.2).
 * Accessibile solo agli amministratori tramite /admin/performance.
 */
@Controller
public class PerformanceController {

    @Autowired
    private PerformanceService performanceService;

    @GetMapping("/admin/performance")
    public String showPerformanceAnalysis(Model model) {
        List<StrategiaResult> risultati = performanceService.runFullAnalysis();
        model.addAttribute("risultati", risultati);

        // Trova il risultato più veloce per evidenziarlo
        long minTempo = risultati.stream()
                .mapToLong(StrategiaResult::getTempoMs)
                .min()
                .orElse(0);
        model.addAttribute("minTempo", minTempo);

        return "admin/performanceAnalysis";
    }
}
