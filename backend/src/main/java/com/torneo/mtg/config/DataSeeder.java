package com.torneo.mtg.config;

import com.torneo.mtg.domain.Participant;
import com.torneo.mtg.domain.Tournament;
import com.torneo.mtg.domain.User;
import com.torneo.mtg.repo.ParticipantRepository;
import com.torneo.mtg.repo.TournamentRepository;
import com.torneo.mtg.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;
    private final ParticipantRepository participantRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(
            UserRepository userRepository,
            TournamentRepository tournamentRepository,
            ParticipantRepository participantRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tournamentRepository = tournamentRepository;
        this.participantRepository = participantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Seed Admin: dcagua268@gmail.com / Holasoyyo12345
        String adminEmail = "dcagua268@gmail.com";
        User admin = userRepository.findByEmail(adminEmail).orElseGet(() -> {
            User u = new User(adminEmail, "David Cagua (Admin)", passwordEncoder.encode("Holasoyyo12345"), "ADMIN");
            return userRepository.save(u);
        });

        // 2. Seed Default Tournament if none exists
        if (tournamentRepository.count() == 0) {
            Tournament tournament = new Tournament("Gran Torneo MTG Modern", "MODERN", 3);
            tournament = tournamentRepository.save(tournament);

            // 3. Seed Participants
            List<String[]> playersData = List.of(
                    new String[]{"carlos@mtg.com", "Carlos Mendoza", "Izzet Murktide"},
                    new String[]{"ana@mtg.com", "Ana Torres", "Boros Energy"},
                    new String[]{"mateo@mtg.com", "Mateo Silva", "Rakdos Scam"},
                    new String[]{"lucia@mtg.com", "Lucia Gomez", "Amulet Titan"},
                    new String[]{"esteban@mtg.com", "Esteban Rivas", "Golgari Yawgmoth"},
                    new String[]{"valentina@mtg.com", "Valentina Paz", "Tron"},
                    new String[]{"julian@mtg.com", "Julian Ortiz", "Dimir Murktide"},
                    new String[]{"camila@mtg.com", "Camila Ruiz", "Domain Zoo"}
            );

            for (String[] data : playersData) {
                User player = userRepository.save(new User(data[0], data[1], passwordEncoder.encode("Jugador123"), "PLAYER"));
                participantRepository.save(new Participant(tournament, player, data[2]));
            }
        }
    }
}
