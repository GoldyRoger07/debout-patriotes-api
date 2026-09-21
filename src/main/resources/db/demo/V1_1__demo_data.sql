-- Données de démonstration (profil « dev » uniquement) : profils FICTIFS repris de l'ancien
-- fichier candidates.fr.ts du site, et un article d'exemple. Ne pas activer en production.

INSERT INTO candidate (slug, name, subtitle, photo_url, position, constituency, party, profession, birthplace, quote, email, facebook, x, instagram, display_order, published, created_at, updated_at)
VALUES ('jean-robert-pierre-louis', 'Jean-Robert Pierre-Louis', 'Candidat à la présidence', NULL, 'Président de la République', 'Circonscription nationale', 'DEBOUT PATRIOTES', 'Économiste', 'Port-au-Prince', 'Se tenir debout, c''est refuser que la résignation décide à notre place.', 'jean-robert-pierre-louis@deboutpatriotes.ht', NULL, NULL, NULL, 1, b'1', NOW(6), NOW(6));
SET @id = LAST_INSERT_ID();
INSERT INTO candidate_bio (candidate_id, sort_order, paragraph) VALUES
    (@id, 0, 'Né à Port-au-Prince, Jean-Robert Pierre-Louis a grandi au contact des réalités de sa communauté, où il s’est très tôt engagé dans la vie associative.'),
    (@id, 1, 'Économiste de métier, Jean-Robert met depuis plus de quinze ans ses compétences au service de la population, sur le terrain comme dans les institutions.'),
    (@id, 2, 'Membre de DEBOUT PATRIOTES dès sa constitution, il porte aujourd’hui les couleurs du groupement pour la présidence de la République.');
INSERT INTO candidate_priority (candidate_id, sort_order, title, description, icon) VALUES
    (@id, 0, 'Sécurité des familles', 'Rétablir la présence de l''État et protéger les quartiers comme les sections communales.', 'pi-shield'),
    (@id, 1, 'Santé de proximité', 'Rouvrir et équiper les centres de santé communautaires.', 'pi-heart'),
    (@id, 2, 'Transparence', 'Rendre compte publiquement de chaque gourde dépensée.', 'pi-eye');
INSERT INTO candidate_career (candidate_id, sort_order, period, title, description) VALUES
    (@id, 0, '2026', 'Investi candidat de DEBOUT PATRIOTES', 'Candidat à la présidence'),
    (@id, 1, '2018 — 2026', 'Économiste — responsable de projets', 'Coordination de programmes au bénéfice des communautés locales.'),
    (@id, 2, '2010 — 2018', 'Engagement associatif', 'Animation d’organisations citoyennes à Port-au-Prince.');
INSERT INTO candidate_education (candidate_id, sort_order, entry) VALUES
    (@id, 0, 'Licence — Université d’État d’Haïti'),
    (@id, 1, 'Certificat en gouvernance locale');

INSERT INTO candidate (slug, name, subtitle, photo_url, position, constituency, party, profession, birthplace, quote, email, facebook, x, instagram, display_order, published, created_at, updated_at)
VALUES ('marie-claude-joseph', 'Marie-Claude Joseph', 'Candidate au Sénat · Ouest', NULL, 'Sénatrice', 'Département de l''Ouest', 'DEBOUT PATRIOTES', 'Avocate', 'Pétion-Ville', 'Servir Haïti, c''est d''abord écouter celles et ceux qui la font vivre chaque jour.', 'marie-claude-joseph@deboutpatriotes.ht', NULL, NULL, NULL, 2, b'1', NOW(6), NOW(6));
SET @id = LAST_INSERT_ID();
INSERT INTO candidate_bio (candidate_id, sort_order, paragraph) VALUES
    (@id, 0, 'Née à Pétion-Ville, Marie-Claude Joseph a grandi au contact des réalités de sa communauté, où elle s’est très tôt engagée dans la vie associative.'),
    (@id, 1, 'Avocate de métier, Marie-Claude met depuis plus de quinze ans ses compétences au service de la population, sur le terrain comme dans les institutions.'),
    (@id, 2, 'Membre de DEBOUT PATRIOTES dès sa constitution, elle porte aujourd’hui les couleurs du groupement pour le poste de sénatrice (Département de l''Ouest).');
INSERT INTO candidate_priority (candidate_id, sort_order, title, description, icon) VALUES
    (@id, 0, 'Emploi des jeunes', 'Soutenir la formation professionnelle et l''entrepreneuriat local.', 'pi-briefcase'),
    (@id, 1, 'École pour tous', 'Garantir l''accès à une éducation de qualité dans chaque commune.', 'pi-book'),
    (@id, 2, 'Diaspora', 'Associer la diaspora à la reconstruction du pays.', 'pi-globe');
INSERT INTO candidate_career (candidate_id, sort_order, period, title, description) VALUES
    (@id, 0, '2026', 'Investie candidate de DEBOUT PATRIOTES', 'Candidate au Sénat · Ouest'),
    (@id, 1, '2018 — 2026', 'Avocate — responsable de projets', 'Coordination de programmes au bénéfice des communautés locales.'),
    (@id, 2, '2010 — 2018', 'Engagement associatif', 'Animation d’organisations citoyennes à Pétion-Ville.');
INSERT INTO candidate_education (candidate_id, sort_order, entry) VALUES
    (@id, 0, 'Licence — Université d’État d’Haïti'),
    (@id, 1, 'Certificat en gouvernance locale');

INSERT INTO candidate (slug, name, subtitle, photo_url, position, constituency, party, profession, birthplace, quote, email, facebook, x, instagram, display_order, published, created_at, updated_at)
VALUES ('wilner-saint-fleur', 'Wilner Saint-Fleur', 'Candidat au Sénat · Artibonite', NULL, 'Sénateur', 'Département de l''Artibonite', 'DEBOUT PATRIOTES', 'Agronome', 'Saint-Marc', 'Se tenir debout, c''est refuser que la résignation décide à notre place.', 'wilner-saint-fleur@deboutpatriotes.ht', NULL, NULL, NULL, 3, b'1', NOW(6), NOW(6));
SET @id = LAST_INSERT_ID();
INSERT INTO candidate_bio (candidate_id, sort_order, paragraph) VALUES
    (@id, 0, 'Né à Saint-Marc, Wilner Saint-Fleur a grandi au contact des réalités de sa communauté, où il s’est très tôt engagé dans la vie associative.'),
    (@id, 1, 'Agronome de métier, Wilner met depuis plus de quinze ans ses compétences au service de la population, sur le terrain comme dans les institutions.'),
    (@id, 2, 'Membre de DEBOUT PATRIOTES dès sa constitution, il porte aujourd’hui les couleurs du groupement pour le poste de sénateur (Département de l''Artibonite).');
INSERT INTO candidate_priority (candidate_id, sort_order, title, description, icon) VALUES
    (@id, 0, 'Production nationale', 'Relancer l''agriculture et la transformation locale des produits.', 'pi-sun'),
    (@id, 1, 'Infrastructures', 'Routes, eau potable et électricité pour désenclaver le territoire.', 'pi-building'),
    (@id, 2, 'Sécurité des familles', 'Rétablir la présence de l''État et protéger les quartiers comme les sections communales.', 'pi-shield');
INSERT INTO candidate_career (candidate_id, sort_order, period, title, description) VALUES
    (@id, 0, '2026', 'Investi candidat de DEBOUT PATRIOTES', 'Candidat au Sénat · Artibonite'),
    (@id, 1, '2018 — 2026', 'Agronome — responsable de projets', 'Coordination de programmes au bénéfice des communautés locales.'),
    (@id, 2, '2010 — 2018', 'Engagement associatif', 'Animation d’organisations citoyennes à Saint-Marc.');
INSERT INTO candidate_education (candidate_id, sort_order, entry) VALUES
    (@id, 0, 'Licence — Université d’État d’Haïti'),
    (@id, 1, 'Certificat en gouvernance locale');

INSERT INTO candidate (slug, name, subtitle, photo_url, position, constituency, party, profession, birthplace, quote, email, facebook, x, instagram, display_order, published, created_at, updated_at)
VALUES ('nadege-celestin', 'Nadège Célestin', 'Candidate à la députation · Port-au-Prince', NULL, 'Députée', 'Circonscription de Port-au-Prince', 'DEBOUT PATRIOTES', 'Médecin', 'Port-au-Prince', 'Servir Haïti, c''est d''abord écouter celles et ceux qui la font vivre chaque jour.', 'nadege-celestin@deboutpatriotes.ht', NULL, NULL, NULL, 4, b'1', NOW(6), NOW(6));
SET @id = LAST_INSERT_ID();
INSERT INTO candidate_bio (candidate_id, sort_order, paragraph) VALUES
    (@id, 0, 'Née à Port-au-Prince, Nadège Célestin a grandi au contact des réalités de sa communauté, où elle s’est très tôt engagée dans la vie associative.'),
    (@id, 1, 'Médecin de métier, Nadège met depuis plus de quinze ans ses compétences au service de la population, sur le terrain comme dans les institutions.'),
    (@id, 2, 'Membre de DEBOUT PATRIOTES dès sa constitution, elle porte aujourd’hui les couleurs du groupement pour le poste de députée (Circonscription de Port-au-Prince).');
INSERT INTO candidate_priority (candidate_id, sort_order, title, description, icon) VALUES
    (@id, 0, 'Santé de proximité', 'Rouvrir et équiper les centres de santé communautaires.', 'pi-heart'),
    (@id, 1, 'Transparence', 'Rendre compte publiquement de chaque gourde dépensée.', 'pi-eye'),
    (@id, 2, 'Emploi des jeunes', 'Soutenir la formation professionnelle et l''entrepreneuriat local.', 'pi-briefcase');
INSERT INTO candidate_career (candidate_id, sort_order, period, title, description) VALUES
    (@id, 0, '2026', 'Investie candidate de DEBOUT PATRIOTES', 'Candidate à la députation · Port-au-Prince'),
    (@id, 1, '2018 — 2026', 'Médecin — responsable de projets', 'Coordination de programmes au bénéfice des communautés locales.'),
    (@id, 2, '2010 — 2018', 'Engagement associatif', 'Animation d’organisations citoyennes à Port-au-Prince.');
INSERT INTO candidate_education (candidate_id, sort_order, entry) VALUES
    (@id, 0, 'Licence — Université d’État d’Haïti'),
    (@id, 1, 'Certificat en gouvernance locale');

INSERT INTO candidate (slug, name, subtitle, photo_url, position, constituency, party, profession, birthplace, quote, email, facebook, x, instagram, display_order, published, created_at, updated_at)
VALUES ('frantz-desir', 'Frantz Désir', 'Candidat au Sénat · Nord', NULL, 'Sénateur', 'Département du Nord', 'DEBOUT PATRIOTES', 'Ingénieur civil', 'Limbé', 'Se tenir debout, c''est refuser que la résignation décide à notre place.', 'frantz-desir@deboutpatriotes.ht', NULL, NULL, NULL, 5, b'1', NOW(6), NOW(6));
SET @id = LAST_INSERT_ID();
INSERT INTO candidate_bio (candidate_id, sort_order, paragraph) VALUES
    (@id, 0, 'Né à Limbé, Frantz Désir a grandi au contact des réalités de sa communauté, où il s’est très tôt engagé dans la vie associative.'),
    (@id, 1, 'Ingénieur civil de métier, Frantz met depuis plus de quinze ans ses compétences au service de la population, sur le terrain comme dans les institutions.'),
    (@id, 2, 'Membre de DEBOUT PATRIOTES dès sa constitution, il porte aujourd’hui les couleurs du groupement pour le poste de sénateur (Département du Nord).');
INSERT INTO candidate_priority (candidate_id, sort_order, title, description, icon) VALUES
    (@id, 0, 'École pour tous', 'Garantir l''accès à une éducation de qualité dans chaque commune.', 'pi-book'),
    (@id, 1, 'Diaspora', 'Associer la diaspora à la reconstruction du pays.', 'pi-globe'),
    (@id, 2, 'Production nationale', 'Relancer l''agriculture et la transformation locale des produits.', 'pi-sun');
INSERT INTO candidate_career (candidate_id, sort_order, period, title, description) VALUES
    (@id, 0, '2026', 'Investi candidat de DEBOUT PATRIOTES', 'Candidat au Sénat · Nord'),
    (@id, 1, '2018 — 2026', 'Ingénieur civil — responsable de projets', 'Coordination de programmes au bénéfice des communautés locales.'),
    (@id, 2, '2010 — 2018', 'Engagement associatif', 'Animation d’organisations citoyennes à Limbé.');
INSERT INTO candidate_education (candidate_id, sort_order, entry) VALUES
    (@id, 0, 'Licence — Université d’État d’Haïti'),
    (@id, 1, 'Certificat en gouvernance locale');

INSERT INTO candidate (slug, name, subtitle, photo_url, position, constituency, party, profession, birthplace, quote, email, facebook, x, instagram, display_order, published, created_at, updated_at)
VALUES ('guerline-augustin', 'Guerline Augustin', 'Candidate à la députation · Jacmel', NULL, 'Députée', 'Circonscription de Jacmel', 'DEBOUT PATRIOTES', 'Enseignante', 'Jacmel', 'Servir Haïti, c''est d''abord écouter celles et ceux qui la font vivre chaque jour.', 'guerline-augustin@deboutpatriotes.ht', NULL, NULL, NULL, 6, b'1', NOW(6), NOW(6));
SET @id = LAST_INSERT_ID();
INSERT INTO candidate_bio (candidate_id, sort_order, paragraph) VALUES
    (@id, 0, 'Née à Jacmel, Guerline Augustin a grandi au contact des réalités de sa communauté, où elle s’est très tôt engagée dans la vie associative.'),
    (@id, 1, 'Enseignante de métier, Guerline met depuis plus de quinze ans ses compétences au service de la population, sur le terrain comme dans les institutions.'),
    (@id, 2, 'Membre de DEBOUT PATRIOTES dès sa constitution, elle porte aujourd’hui les couleurs du groupement pour le poste de députée (Circonscription de Jacmel).');
INSERT INTO candidate_priority (candidate_id, sort_order, title, description, icon) VALUES
    (@id, 0, 'Infrastructures', 'Routes, eau potable et électricité pour désenclaver le territoire.', 'pi-building'),
    (@id, 1, 'Sécurité des familles', 'Rétablir la présence de l''État et protéger les quartiers comme les sections communales.', 'pi-shield'),
    (@id, 2, 'Santé de proximité', 'Rouvrir et équiper les centres de santé communautaires.', 'pi-heart');
INSERT INTO candidate_career (candidate_id, sort_order, period, title, description) VALUES
    (@id, 0, '2026', 'Investie candidate de DEBOUT PATRIOTES', 'Candidate à la députation · Jacmel'),
    (@id, 1, '2018 — 2026', 'Enseignante — responsable de projets', 'Coordination de programmes au bénéfice des communautés locales.'),
    (@id, 2, '2010 — 2018', 'Engagement associatif', 'Animation d’organisations citoyennes à Jacmel.');
INSERT INTO candidate_education (candidate_id, sort_order, entry) VALUES
    (@id, 0, 'Licence — Université d’État d’Haïti'),
    (@id, 1, 'Certificat en gouvernance locale');

INSERT INTO candidate (slug, name, subtitle, photo_url, position, constituency, party, profession, birthplace, quote, email, facebook, x, instagram, display_order, published, created_at, updated_at)
VALUES ('ricardo-beauvais', 'Ricardo Beauvais', 'Candidat à la députation · Les Cayes', NULL, 'Député', 'Circonscription des Cayes', 'DEBOUT PATRIOTES', 'Entrepreneur', 'Les Cayes', 'Se tenir debout, c''est refuser que la résignation décide à notre place.', 'ricardo-beauvais@deboutpatriotes.ht', NULL, NULL, NULL, 7, b'1', NOW(6), NOW(6));
SET @id = LAST_INSERT_ID();
INSERT INTO candidate_bio (candidate_id, sort_order, paragraph) VALUES
    (@id, 0, 'Né à Les Cayes, Ricardo Beauvais a grandi au contact des réalités de sa communauté, où il s’est très tôt engagé dans la vie associative.'),
    (@id, 1, 'Entrepreneur de métier, Ricardo met depuis plus de quinze ans ses compétences au service de la population, sur le terrain comme dans les institutions.'),
    (@id, 2, 'Membre de DEBOUT PATRIOTES dès sa constitution, il porte aujourd’hui les couleurs du groupement pour le poste de député (Circonscription des Cayes).');
INSERT INTO candidate_priority (candidate_id, sort_order, title, description, icon) VALUES
    (@id, 0, 'Transparence', 'Rendre compte publiquement de chaque gourde dépensée.', 'pi-eye'),
    (@id, 1, 'Emploi des jeunes', 'Soutenir la formation professionnelle et l''entrepreneuriat local.', 'pi-briefcase'),
    (@id, 2, 'École pour tous', 'Garantir l''accès à une éducation de qualité dans chaque commune.', 'pi-book');
INSERT INTO candidate_career (candidate_id, sort_order, period, title, description) VALUES
    (@id, 0, '2026', 'Investi candidat de DEBOUT PATRIOTES', 'Candidat à la députation · Les Cayes'),
    (@id, 1, '2018 — 2026', 'Entrepreneur — responsable de projets', 'Coordination de programmes au bénéfice des communautés locales.'),
    (@id, 2, '2010 — 2018', 'Engagement associatif', 'Animation d’organisations citoyennes à Les Cayes.');
INSERT INTO candidate_education (candidate_id, sort_order, entry) VALUES
    (@id, 0, 'Licence — Université d’État d’Haïti'),
    (@id, 1, 'Certificat en gouvernance locale');

INSERT INTO candidate (slug, name, subtitle, photo_url, position, constituency, party, profession, birthplace, quote, email, facebook, x, instagram, display_order, published, created_at, updated_at)
VALUES ('stephanie-lamour', 'Stéphanie Lamour', 'Candidate au Sénat · Sud-Est', NULL, 'Sénatrice', 'Département du Sud-Est', 'DEBOUT PATRIOTES', 'Journaliste', 'Bainet', 'Servir Haïti, c''est d''abord écouter celles et ceux qui la font vivre chaque jour.', 'stephanie-lamour@deboutpatriotes.ht', NULL, NULL, NULL, 8, b'1', NOW(6), NOW(6));
SET @id = LAST_INSERT_ID();
INSERT INTO candidate_bio (candidate_id, sort_order, paragraph) VALUES
    (@id, 0, 'Née à Bainet, Stéphanie Lamour a grandi au contact des réalités de sa communauté, où elle s’est très tôt engagée dans la vie associative.'),
    (@id, 1, 'Journaliste de métier, Stéphanie met depuis plus de quinze ans ses compétences au service de la population, sur le terrain comme dans les institutions.'),
    (@id, 2, 'Membre de DEBOUT PATRIOTES dès sa constitution, elle porte aujourd’hui les couleurs du groupement pour le poste de sénatrice (Département du Sud-Est).');
INSERT INTO candidate_priority (candidate_id, sort_order, title, description, icon) VALUES
    (@id, 0, 'Diaspora', 'Associer la diaspora à la reconstruction du pays.', 'pi-globe'),
    (@id, 1, 'Production nationale', 'Relancer l''agriculture et la transformation locale des produits.', 'pi-sun'),
    (@id, 2, 'Infrastructures', 'Routes, eau potable et électricité pour désenclaver le territoire.', 'pi-building');
INSERT INTO candidate_career (candidate_id, sort_order, period, title, description) VALUES
    (@id, 0, '2026', 'Investie candidate de DEBOUT PATRIOTES', 'Candidate au Sénat · Sud-Est'),
    (@id, 1, '2018 — 2026', 'Journaliste — responsable de projets', 'Coordination de programmes au bénéfice des communautés locales.'),
    (@id, 2, '2010 — 2018', 'Engagement associatif', 'Animation d’organisations citoyennes à Bainet.');
INSERT INTO candidate_education (candidate_id, sort_order, entry) VALUES
    (@id, 0, 'Licence — Université d’État d’Haïti'),
    (@id, 1, 'Certificat en gouvernance locale');

INSERT INTO candidate (slug, name, subtitle, photo_url, position, constituency, party, profession, birthplace, quote, email, facebook, x, instagram, display_order, published, created_at, updated_at)
VALUES ('emmanuel-jean-baptiste', 'Emmanuel Jean-Baptiste', 'Candidat à la députation · Cap-Haïtien', NULL, 'Député', 'Circonscription du Cap-Haïtien', 'DEBOUT PATRIOTES', 'Gestionnaire portuaire', 'Cap-Haïtien', 'Se tenir debout, c''est refuser que la résignation décide à notre place.', 'emmanuel-jean-baptiste@deboutpatriotes.ht', NULL, NULL, NULL, 9, b'1', NOW(6), NOW(6));
SET @id = LAST_INSERT_ID();
INSERT INTO candidate_bio (candidate_id, sort_order, paragraph) VALUES
    (@id, 0, 'Né à Cap-Haïtien, Emmanuel Jean-Baptiste a grandi au contact des réalités de sa communauté, où il s’est très tôt engagé dans la vie associative.'),
    (@id, 1, 'Gestionnaire portuaire de métier, Emmanuel met depuis plus de quinze ans ses compétences au service de la population, sur le terrain comme dans les institutions.'),
    (@id, 2, 'Membre de DEBOUT PATRIOTES dès sa constitution, il porte aujourd’hui les couleurs du groupement pour le poste de député (Circonscription du Cap-Haïtien).');
INSERT INTO candidate_priority (candidate_id, sort_order, title, description, icon) VALUES
    (@id, 0, 'Sécurité des familles', 'Rétablir la présence de l''État et protéger les quartiers comme les sections communales.', 'pi-shield'),
    (@id, 1, 'Santé de proximité', 'Rouvrir et équiper les centres de santé communautaires.', 'pi-heart'),
    (@id, 2, 'Transparence', 'Rendre compte publiquement de chaque gourde dépensée.', 'pi-eye');
INSERT INTO candidate_career (candidate_id, sort_order, period, title, description) VALUES
    (@id, 0, '2026', 'Investi candidat de DEBOUT PATRIOTES', 'Candidat à la députation · Cap-Haïtien'),
    (@id, 1, '2018 — 2026', 'Gestionnaire portuaire — responsable de projets', 'Coordination de programmes au bénéfice des communautés locales.'),
    (@id, 2, '2010 — 2018', 'Engagement associatif', 'Animation d’organisations citoyennes à Cap-Haïtien.');
INSERT INTO candidate_education (candidate_id, sort_order, entry) VALUES
    (@id, 0, 'Licence — Université d’État d’Haïti'),
    (@id, 1, 'Certificat en gouvernance locale');

INSERT INTO candidate (slug, name, subtitle, photo_url, position, constituency, party, profession, birthplace, quote, email, facebook, x, instagram, display_order, published, created_at, updated_at)
VALUES ('rose-andree-michel', 'Rose-Andrée Michel', 'Candidate au Sénat · Grand''Anse', NULL, 'Sénatrice', 'Département de la Grand''Anse', 'DEBOUT PATRIOTES', 'Infirmière', 'Jérémie', 'Servir Haïti, c''est d''abord écouter celles et ceux qui la font vivre chaque jour.', 'rose-andree-michel@deboutpatriotes.ht', NULL, NULL, NULL, 10, b'1', NOW(6), NOW(6));
SET @id = LAST_INSERT_ID();
INSERT INTO candidate_bio (candidate_id, sort_order, paragraph) VALUES
    (@id, 0, 'Née à Jérémie, Rose-Andrée Michel a grandi au contact des réalités de sa communauté, où elle s’est très tôt engagée dans la vie associative.'),
    (@id, 1, 'Infirmière de métier, Rose-Andrée met depuis plus de quinze ans ses compétences au service de la population, sur le terrain comme dans les institutions.'),
    (@id, 2, 'Membre de DEBOUT PATRIOTES dès sa constitution, elle porte aujourd’hui les couleurs du groupement pour le poste de sénatrice (Département de la Grand''Anse).');
INSERT INTO candidate_priority (candidate_id, sort_order, title, description, icon) VALUES
    (@id, 0, 'Emploi des jeunes', 'Soutenir la formation professionnelle et l''entrepreneuriat local.', 'pi-briefcase'),
    (@id, 1, 'École pour tous', 'Garantir l''accès à une éducation de qualité dans chaque commune.', 'pi-book'),
    (@id, 2, 'Diaspora', 'Associer la diaspora à la reconstruction du pays.', 'pi-globe');
INSERT INTO candidate_career (candidate_id, sort_order, period, title, description) VALUES
    (@id, 0, '2026', 'Investie candidate de DEBOUT PATRIOTES', 'Candidate au Sénat · Grand''Anse'),
    (@id, 1, '2018 — 2026', 'Infirmière — responsable de projets', 'Coordination de programmes au bénéfice des communautés locales.'),
    (@id, 2, '2010 — 2018', 'Engagement associatif', 'Animation d’organisations citoyennes à Jérémie.');
INSERT INTO candidate_education (candidate_id, sort_order, entry) VALUES
    (@id, 0, 'Licence — Université d’État d’Haïti'),
    (@id, 1, 'Certificat en gouvernance locale');

INSERT INTO candidate (slug, name, subtitle, photo_url, position, constituency, party, profession, birthplace, quote, email, facebook, x, instagram, display_order, published, created_at, updated_at)
VALUES ('patrick-dorvil', 'Patrick Dorvil', 'Candidat à la députation · Hinche', NULL, 'Député', 'Circonscription de Hinche', 'DEBOUT PATRIOTES', 'Animateur communautaire', 'Hinche', 'Se tenir debout, c''est refuser que la résignation décide à notre place.', 'patrick-dorvil@deboutpatriotes.ht', NULL, NULL, NULL, 11, b'1', NOW(6), NOW(6));
SET @id = LAST_INSERT_ID();
INSERT INTO candidate_bio (candidate_id, sort_order, paragraph) VALUES
    (@id, 0, 'Né à Hinche, Patrick Dorvil a grandi au contact des réalités de sa communauté, où il s’est très tôt engagé dans la vie associative.'),
    (@id, 1, 'Animateur communautaire de métier, Patrick met depuis plus de quinze ans ses compétences au service de la population, sur le terrain comme dans les institutions.'),
    (@id, 2, 'Membre de DEBOUT PATRIOTES dès sa constitution, il porte aujourd’hui les couleurs du groupement pour le poste de député (Circonscription de Hinche).');
INSERT INTO candidate_priority (candidate_id, sort_order, title, description, icon) VALUES
    (@id, 0, 'Production nationale', 'Relancer l''agriculture et la transformation locale des produits.', 'pi-sun'),
    (@id, 1, 'Infrastructures', 'Routes, eau potable et électricité pour désenclaver le territoire.', 'pi-building'),
    (@id, 2, 'Sécurité des familles', 'Rétablir la présence de l''État et protéger les quartiers comme les sections communales.', 'pi-shield');
INSERT INTO candidate_career (candidate_id, sort_order, period, title, description) VALUES
    (@id, 0, '2026', 'Investi candidat de DEBOUT PATRIOTES', 'Candidat à la députation · Hinche'),
    (@id, 1, '2018 — 2026', 'Animateur communautaire — responsable de projets', 'Coordination de programmes au bénéfice des communautés locales.'),
    (@id, 2, '2010 — 2018', 'Engagement associatif', 'Animation d’organisations citoyennes à Hinche.');
INSERT INTO candidate_education (candidate_id, sort_order, entry) VALUES
    (@id, 0, 'Licence — Université d’État d’Haïti'),
    (@id, 1, 'Certificat en gouvernance locale');

INSERT INTO candidate (slug, name, subtitle, photo_url, position, constituency, party, profession, birthplace, quote, email, facebook, x, instagram, display_order, published, created_at, updated_at)
VALUES ('fabienne-toussaint', 'Fabienne Toussaint', 'Candidate au Sénat · Nord-Est', NULL, 'Sénatrice', 'Département du Nord-Est', 'DEBOUT PATRIOTES', 'Juriste', 'Fort-Liberté', 'Servir Haïti, c''est d''abord écouter celles et ceux qui la font vivre chaque jour.', 'fabienne-toussaint@deboutpatriotes.ht', NULL, NULL, NULL, 12, b'1', NOW(6), NOW(6));
SET @id = LAST_INSERT_ID();
INSERT INTO candidate_bio (candidate_id, sort_order, paragraph) VALUES
    (@id, 0, 'Née à Fort-Liberté, Fabienne Toussaint a grandi au contact des réalités de sa communauté, où elle s’est très tôt engagée dans la vie associative.'),
    (@id, 1, 'Juriste de métier, Fabienne met depuis plus de quinze ans ses compétences au service de la population, sur le terrain comme dans les institutions.'),
    (@id, 2, 'Membre de DEBOUT PATRIOTES dès sa constitution, elle porte aujourd’hui les couleurs du groupement pour le poste de sénatrice (Département du Nord-Est).');
INSERT INTO candidate_priority (candidate_id, sort_order, title, description, icon) VALUES
    (@id, 0, 'Santé de proximité', 'Rouvrir et équiper les centres de santé communautaires.', 'pi-heart'),
    (@id, 1, 'Transparence', 'Rendre compte publiquement de chaque gourde dépensée.', 'pi-eye'),
    (@id, 2, 'Emploi des jeunes', 'Soutenir la formation professionnelle et l''entrepreneuriat local.', 'pi-briefcase');
INSERT INTO candidate_career (candidate_id, sort_order, period, title, description) VALUES
    (@id, 0, '2026', 'Investie candidate de DEBOUT PATRIOTES', 'Candidate au Sénat · Nord-Est'),
    (@id, 1, '2018 — 2026', 'Juriste — responsable de projets', 'Coordination de programmes au bénéfice des communautés locales.'),
    (@id, 2, '2010 — 2018', 'Engagement associatif', 'Animation d’organisations citoyennes à Fort-Liberté.');
INSERT INTO candidate_education (candidate_id, sort_order, entry) VALUES
    (@id, 0, 'Licence — Université d’État d’Haïti'),
    (@id, 1, 'Certificat en gouvernance locale');

INSERT INTO post (slug, title, excerpt, content, category_id, status, published_at, created_at, updated_at)
VALUES ('bienvenue-sur-le-blog', 'Bienvenue sur le blog de DEBOUT PATRIOTES',
        'Article de démonstration : il illustre la mise en forme disponible dans le back-office.',
        '## Un espace d''information

Ce blog publie les **prises de position**, les *activités de terrain* et la vie des onze partis membres.

- Communiqués officiels
- Comptes rendus de rencontres départementales
- Étapes de la marche vers les élections

> Se tenir debout, c''est refuser que la résignation décide à notre place.

Cet article peut être modifié ou supprimé depuis `/admin/articles`.',
        (SELECT id FROM category WHERE slug = 'vie-du-groupement'), 'PUBLISHED', NOW(6), NOW(6), NOW(6));
