package Pb;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Pb_Main {
    public static void scriere_lista(List lista) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("src/main/resources/angajati.json");
            mapper.writeValue(file, lista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List citire_lista() {
        try {
            File file = new File("src/main/resources/angajati.json");
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.<List<Angajat>>readValue(file, new TypeReference<>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void afisare_filtrata(List<Angajat> lista, Predicate<Angajat> f) {
        for (Angajat a : lista)
            if (f.test(a))
                System.out.println(a);
    }

    public static void main(String[] args) {
        List<Angajat> lista_angajati = citire_lista();

        //Ex 1
        assert lista_angajati != null;
        lista_angajati.forEach(System.out::println);

        //Ex 2
        System.out.println("\n\nAngajatii cu salariul mai mare de 2500 RON:");
        afisare_filtrata(lista_angajati, a -> a.getSalariul() > 2500);

        //Ex 3
        System.out.println("\n\nAngajatii sefi apr an trecut:");
        List<Angajat> lista_sefi_apr2023 = lista_angajati.stream()
                .filter((angajat) -> angajat.getData_angajarii().getYear() == LocalDate.now().getYear() - 1 &&
                        (angajat.getPostul().contains("sef") || angajat.getPostul().contains("director")))
                .collect(Collectors.toList());
        lista_sefi_apr2023.forEach(System.out::println);

        //Ex 4
        System.out.println("\n\nAngajatii fara functie de conducere:");
        lista_angajati.stream()
                .filter((angajat) -> !(angajat.getPostul().contains("sef") || angajat.getPostul().contains("director")))
                .sorted((a, b) -> Float.valueOf(b.getSalariul()).compareTo(Float.valueOf(a.getSalariul())))
                .forEach(System.out::println);

        //Ex 5
        System.out.println("\n\nNumele angajatilor cu majuscule:");
        List<String> lista_nume_majuscule = lista_angajati.stream()
                .map(Angajat::getNumele)
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        lista_nume_majuscule.forEach(System.out::println);

        //Ex 6
        System.out.println("\n\nLista salarii < 3000 RON:");
        List<Float> lista_salarii = lista_angajati.stream()
                .map(Angajat::getSalariul)
                .filter((a) -> a < 3000)
                .collect(Collectors.toList());
        lista_salarii.forEach(System.out::println);

        //Ex 7
        System.out.println("\n\nPrimul angajat:");
        Optional<Angajat> opt_primulAngajat = lista_angajati
                .stream()
                .min(Comparator.comparing(Angajat::getData_angajarii));
        opt_primulAngajat.ifPresentOrElse(System.out::println, () -> System.out.println("Valoare lipsa"));

        //Ex 8
        System.out.println("\n\nStatistici salarii:");
        DoubleSummaryStatistics statisticiSalariu = lista_angajati
                .stream()
                .map(Angajat::getSalariul)
                .collect(Collectors.summarizingDouble(Float::doubleValue));
        System.out.println("Min: " + statisticiSalariu.getMin());
        System.out.println("Max: " + statisticiSalariu.getMax());
        System.out.println("Average: " + statisticiSalariu.getAverage());

        //Ex 9
        System.out.println("\n\nAngajatul Ion:");
        Optional<Angajat> angajatIon = lista_angajati.stream()
                .filter(angajat -> "Ion".equals(angajat.getNumele()))
                .findAny();
        angajatIon.ifPresentOrElse(System.out::println, () -> System.out.println("Valoare lipsa"));

        //Ex 10
        long numarAngajatiVara = lista_angajati.stream()
                .filter(angajat -> angajat.getData_angajarii().getYear() == LocalDate.now().getYear() - 1)
                .filter(angajat -> {
                    int luna = angajat.getData_angajarii().getMonthValue();
                    return luna >= 6 && luna <= 8; //Iunie, Iulie, August
                })
                .count();
        System.out.println("\n\nPersoane angajate vara precedenta: " + numarAngajatiVara);
    }
}
