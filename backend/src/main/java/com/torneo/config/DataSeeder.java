package com.torneo.config;

import com.torneo.domain.entity.ParticipantEntity;
import com.torneo.domain.entity.TournamentEntity;
import com.torneo.domain.entity.UserEntity;
import com.torneo.domain.model.Role;
import com.torneo.domain.model.TournamentStatus;
import com.torneo.infrastructure.persistence.repository.ParticipantRepository;
import com.torneo.infrastructure.persistence.repository.TournamentRepository;
import com.torneo.infrastructure.persistence.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;
    private final ParticipantRepository participantRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:dcagua268@gmail.com}")
    private String adminEmail;

    @Value("${app.admin.password:Holasoyyo12345}")
    private String adminPassword;

    public DataSeeder(
            UserRepository userRepository,
            TournamentRepository tournamentRepository,
            ParticipantRepository participantRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.tournamentRepository = tournamentRepository;
        this.participantRepository = participantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdminUser();
        seedClientUser();
        seedInitialTournamentAndParticipants();
    }

    private void seedAdminUser() {
        String email = adminEmail.trim().toLowerCase();
        if (!userRepository.existsByEmail(email)) {
            UserEntity admin = new UserEntity();
            admin.setId(UUID.randomUUID().toString());
            admin.setEmail(email);
            admin.setName("Daniel Cagua (Administrador)");
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setAuthProvider("LOCAL");
            admin.setCreatedAt(Instant.now());
            userRepository.save(admin);
            log.info(">>> Administrador inicial creado exitosamente: {} con rol ADMIN", email);
        } else {
            // Update admin role and password if needed
            userRepository.findByEmail(email).ifPresent(u -> {
                u.setRole(Role.ADMIN);
                u.setPasswordHash(passwordEncoder.encode(adminPassword));
                userRepository.save(u);
            });
        }
    }

    private void seedClientUser() {
        String clientEmail = "cliente@torneo.com";
        if (!userRepository.existsByEmail(clientEmail)) {
            UserEntity client = new UserEntity();
            client.setId(UUID.randomUUID().toString());
            client.setEmail(clientEmail);
            client.setName("Carlos Cliente");
            client.setPasswordHash(passwordEncoder.encode("Cliente12345"));
            client.setRole(Role.CLIENT);
            client.setAuthProvider("LOCAL");
            client.setCreatedAt(Instant.now());
            userRepository.save(client);
            log.info(">>> Usuario Cliente demo creado: {} con rol CLIENT", clientEmail);
        }
    }

    private void seedInitialTournamentAndParticipants() {
        if (tournamentRepository.count() == 0) {
            TournamentEntity tournament = new TournamentEntity();
            tournament.setId("t-default-01");
            tournament.setName("Torneo Relámpago Oficial 2026");
            tournament.setDescription("Gran Torneo Inaugural abierto a todos los competidores con emparejamiento aleatorio.");
            tournament.setCategory("Competitivo / Abierto");
            tournament.setStatus(TournamentStatus.REGISTRATION);
            tournament.setCreatedAt(Instant.now());
            tournamentRepository.save(tournament);

            // Pre-seed some participants for demo and immediate testing
            List<ParticipantEntity> demoParticipants = List.of(
                    new ParticipantEntity(UUID.randomUUID().toString(), "Alejandro Ruiz", "alejandro.ruiz@gmail.com", "+57 300 111 2233", "CC-102030", "Abierto", tournament),
                    new ParticipantEntity(UUID.randomUUID().toString(), "Beatriz Morales", "beatriz.m@gmail.com", "+57 311 222 3344", "CC-102031", "Abierto", tournament),
                    new ParticipantEntity(UUID.randomUUID().toString(), "Carlos Mendoza", "carlos.m@gmail.com", "+57 312 333 4455", "CC-102032", "Abierto", tournament),
                    new ParticipantEntity(UUID.randomUUID().toString(), "Diana Ospina", "diana.o@gmail.com", "+57 315 444 5566", "CC-102033", "Abierto", tournament),
                    new ParticipantEntity(UUID.randomUUID().toString(), "Esteban Gómez", "esteban.g@gmail.com", "+57 316 555 6677", "CC-102034", "Abierto", tournament),
                    new ParticipantEntity(UUID.randomUUID().toString(), "Fernanda Castro", "fernanda.c@gmail.com", "+57 317 666 7788", "CC-102035", "Abierto", tournament),
                    new ParticipantEntity(UUID.randomUUID().toString(), "Gabriel Torres", "gabriel.t@gmail.com", "+57 318 777 8899", "CC-102036", "Abierto", tournament),
                    new ParticipantEntity(UUID.randomUUID().toString(), "Helena Vargas", "helena.v@gmail.com", "+57 319 888 9900", "CC-102037", "Abierto", tournament)
            );

            int seed = 1;
            for (ParticipantEntity p : demoParticipants) {
                p.setSeedNumber(seed++);
            }

            participantRepository.saveAll(demoParticipants);
            log.info(">>> Torneo demo y 8 participantes iniciales inscritos con éxito para pruebas inmediatas.");
        }
    }
}
