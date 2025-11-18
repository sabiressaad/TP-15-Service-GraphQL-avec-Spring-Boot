package com.example.banque_service.controllers;

import com.example.banque_service.entities.Compte;
import com.example.banque_service.entities.TypeCompte;
import com.example.banque_service.repositories.CompteRepository;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class CompteControllerGraphQL {

    private final CompteRepository compteRepository;

    // =========================
    //        QUERIES
    // =========================

    @QueryMapping
    public List<Compte> allComptes() {
        return compteRepository.findAll();
    }

    @QueryMapping
    public Compte compteById(@Argument Long id) {
        return compteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Compte %s not found", id)
                ));
    }

    @QueryMapping
    public Map<String, Object> totalSolde() {
        long count = compteRepository.count();
        double sum = compteRepository.sumSoldes();
        double average = count > 0 ? sum / count : 0;

        return Map.of(
                "count", count,
                "sum", sum,
                "average", average
        );
    }

    // =========================
    //        MUTATION
    // =========================

    @MutationMapping
    public Compte saveCompte(
            @Argument("compte") Map<String, Object> input) throws Exception {

        double solde = Double.parseDouble(input.get("solde").toString());
        String dateStr = input.get("dateCreation").toString();
        TypeCompte type = TypeCompte.valueOf(input.get("type").toString());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dateCreation = sdf.parse(dateStr);

        Compte c = new Compte(null, solde, dateCreation, type);

        return compteRepository.save(c);
    }
}
