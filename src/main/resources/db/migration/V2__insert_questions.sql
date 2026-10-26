-- Pitanja generirana iz predavanja kolegija "Uvod u programiranje" (ZPR-FER-UNIZG)
-- 50 pitanja na hrvatskom jeziku, trajanje 15 sekundi (default)

-- Pitanje 1
INSERT INTO question (question) VALUES ('Koji su osnovni cjelobrojni tipovi podataka u programskom jeziku C?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'char, int, _Bool', TRUE),
    (currval('question_question_id_seq'), 'float, double, long', FALSE),
    (currval('question_question_id_seq'), 'int, float, string', FALSE),
    (currval('question_question_id_seq'), 'char, float, bool', FALSE);

-- Pitanje 2
INSERT INTO question (question) VALUES ('Koji tip podataka u C-u služi za pohranu realnih brojeva s jednostrukom preciznošću?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'int', FALSE),
    (currval('question_question_id_seq'), 'double', FALSE),
    (currval('question_question_id_seq'), 'float', TRUE),
    (currval('question_question_id_seq'), 'real', FALSE);

-- Pitanje 3
INSERT INTO question (question) VALUES ('Što se dogodi pri implicitnoj pretvorbi tipova kada su operandi int i float?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'float se pretvara u int', FALSE),
    (currval('question_question_id_seq'), 'int se pretvara u float', TRUE),
    (currval('question_question_id_seq'), 'Oba se pretvaraju u double', FALSE),
    (currval('question_question_id_seq'), 'Dolazi do greške', FALSE);

-- Pitanje 4
INSERT INTO question (question) VALUES ('Koliko bajtova zauzima tip podataka float u programskom jeziku C?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), '2 bajta', FALSE),
    (currval('question_question_id_seq'), '4 bajta', TRUE),
    (currval('question_question_id_seq'), '8 bajtova', FALSE),
    (currval('question_question_id_seq'), '1 bajt', FALSE);

-- Pitanje 5
INSERT INTO question (question) VALUES ('Što opisuje tip podatka varijable u programskom jeziku C?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Ime varijable i njenu adresu u memoriji', FALSE),
    (currval('question_question_id_seq'), 'Karakteristike podatka i operacije koje se nad njim mogu obaviti', TRUE),
    (currval('question_question_id_seq'), 'Samo veličinu podatka u bajtovima', FALSE),
    (currval('question_question_id_seq'), 'Vidljivost varijable unutar programa', FALSE);

-- Pitanje 6
INSERT INTO question (question) VALUES ('Što je rezultat cjelobrojnog dijeljenja 7 / 2 u programskom jeziku C?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), '3.5', FALSE),
    (currval('question_question_id_seq'), '4', FALSE),
    (currval('question_question_id_seq'), '3', TRUE),
    (currval('question_question_id_seq'), 'Greška pri prevođenju', FALSE);

-- Pitanje 7
INSERT INTO question (question) VALUES ('Koja petlja u C-u UVIJEK izvršava tijelo barem jednom?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'for petlja', FALSE),
    (currval('question_question_id_seq'), 'while petlja', FALSE),
    (currval('question_question_id_seq'), 'do-while petlja', TRUE),
    (currval('question_question_id_seq'), 'Sve navedene petlje', FALSE);

-- Pitanje 8
INSERT INTO question (question) VALUES ('Kada je naredba switch prikladnija od if-else-if?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Kada testiramo uvjete s realnim brojevima', FALSE),
    (currval('question_question_id_seq'), 'Kada testiramo jednakost cjelobrojnih vrijednosti', TRUE),
    (currval('question_question_id_seq'), 'Kada imamo samo jedan uvjet', FALSE),
    (currval('question_question_id_seq'), 'Kada koristimo logičke operatore', FALSE);

-- Pitanje 9
INSERT INTO question (question) VALUES ('Što radi naredba break unutar petlje?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Preskače ostatak trenutne iteracije i nastavlja sljedeću', FALSE),
    (currval('question_question_id_seq'), 'Pauzira izvršavanje petlje', FALSE),
    (currval('question_question_id_seq'), 'Odmah prekida izvršavanje petlje', TRUE),
    (currval('question_question_id_seq'), 'Restartira petlju od početka', FALSE);

-- Pitanje 10
INSERT INTO question (question) VALUES ('Koja je razlika između while i do-while petlje?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'while petlja je brža od do-while petlje', FALSE),
    (currval('question_question_id_seq'), 'do-while provjerava uvjet na kraju, while na početku', TRUE),
    (currval('question_question_id_seq'), 'do-while se može koristiti samo za beskonačne petlje', FALSE),
    (currval('question_question_id_seq'), 'Nema razlike, radi se o sinonimima', FALSE);

-- Pitanje 11
INSERT INTO question (question) VALUES ('Što je pokazivač (pointer) u programskom jeziku C?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Varijabla koja pohranjuje vrijednost cijelog broja', FALSE),
    (currval('question_question_id_seq'), 'Varijabla koja pohranjuje memorijsku adresu drugog objekta', TRUE),
    (currval('question_question_id_seq'), 'Poseban tip petlje za iteraciju po polju', FALSE),
    (currval('question_question_id_seq'), 'Funkcija za pristup elementima polja', FALSE);

-- Pitanje 12
INSERT INTO question (question) VALUES ('Koji operator se koristi za dohvaćanje adrese varijable u C-u?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), '* (zvjezdica)', FALSE),
    (currval('question_question_id_seq'), '-> (strelica)', FALSE),
    (currval('question_question_id_seq'), '& (ampersand)', TRUE),
    (currval('question_question_id_seq'), '@ (at)', FALSE);

-- Pitanje 13
INSERT INTO question (question) VALUES ('Koji operator se koristi za pristup vrijednosti na adresi na koju pokazuje pokazivač?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), '& (ampersand)', FALSE),
    (currval('question_question_id_seq'), '* (operator indirekcije)', TRUE),
    (currval('question_question_id_seq'), '-> (strelica)', FALSE),
    (currval('question_question_id_seq'), '. (točka)', FALSE);

-- Pitanje 14
INSERT INTO question (question) VALUES ('Koliko prostora zauzima pokazivač na tip int u usporedbi s pokazivačem na tip double?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Pokazivač na int zauzima manje prostora', FALSE),
    (currval('question_question_id_seq'), 'Pokazivač na double zauzima manje prostora', FALSE),
    (currval('question_question_id_seq'), 'Oba zauzimaju jednako prostora', TRUE),
    (currval('question_question_id_seq'), 'Ovisi o vrijednosti pohranjene u varijabli', FALSE);

-- Pitanje 15
INSERT INTO question (question) VALUES ('Što je "curenje memorije" (memory leak) u C-u?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Situacija kada program čita podatke iz pogrešne memorijske lokacije', FALSE),
    (currval('question_question_id_seq'), 'Kada alocirana memorija na gomili nema ni jedan pokazivač koji na nju pokazuje', TRUE),
    (currval('question_question_id_seq'), 'Kada program prekorači veličinu stoga', FALSE),
    (currval('question_question_id_seq'), 'Kada se isti blok memorije oslobodi dva puta', FALSE);

-- Pitanje 16
INSERT INTO question (question) VALUES ('Koja funkcija se koristi za dinamičku alokaciju memorije u C-u?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'alloc()', FALSE),
    (currval('question_question_id_seq'), 'new()', FALSE),
    (currval('question_question_id_seq'), 'malloc()', TRUE),
    (currval('question_question_id_seq'), 'memget()', FALSE);

-- Pitanje 17
INSERT INTO question (question) VALUES ('Što se mora napraviti s dinamički alociranom memorijom nakon što se više ne koristi?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Ništa, C automatski oslobađa memoriju', FALSE),
    (currval('question_question_id_seq'), 'Pozvati funkciju free()', TRUE),
    (currval('question_question_id_seq'), 'Postaviti pokazivač na NULL', FALSE),
    (currval('question_question_id_seq'), 'Pozvati funkciju delete()', FALSE);

-- Pitanje 18
INSERT INTO question (question) VALUES ('Što je rekurzivna funkcija?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Funkcija koja prima drugi funkciju kao argument', FALSE),
    (currval('question_question_id_seq'), 'Funkcija koja poziva samu sebe', TRUE),
    (currval('question_question_id_seq'), 'Funkcija koja ne vraća nikakvu vrijednost', FALSE),
    (currval('question_question_id_seq'), 'Funkcija definirana unutar druge funkcije', FALSE);

-- Pitanje 19
INSERT INTO question (question) VALUES ('Koliko vrijednosti može vratiti funkcija u C-u putem naredbe return?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Neograničen broj vrijednosti', FALSE),
    (currval('question_question_id_seq'), 'Najviše dvije vrijednosti', FALSE),
    (currval('question_question_id_seq'), 'Najviše jednu vrijednost', TRUE),
    (currval('question_question_id_seq'), 'Ovisi o tipu povratne vrijednosti', FALSE);

-- Pitanje 20
INSERT INTO question (question) VALUES ('Što se događa kada rekurzivna funkcija nema uvjet zaustavljanja?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Funkcija se nikad ne pokreće', FALSE),
    (currval('question_question_id_seq'), 'Program radi ispravno, samo sporije', FALSE),
    (currval('question_question_id_seq'), 'Stog se puni dok program ne završi s greškom', TRUE),
    (currval('question_question_id_seq'), 'Prevodilac prijavljuje grešku', FALSE);

-- Pitanje 21
INSERT INTO question (question) VALUES ('Što je agregatni tip podataka u C-u?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Tip koji može pohraniti samo jedan skalarni podatak', FALSE),
    (currval('question_question_id_seq'), 'Tip koji objedinjuje više podataka pod istim imenom (polje, struktura)', TRUE),
    (currval('question_question_id_seq'), 'Poseban naziv za tip pokazivača', FALSE),
    (currval('question_question_id_seq'), 'Tip podataka definiran u standardnoj biblioteci', FALSE);

-- Pitanje 22
INSERT INTO question (question) VALUES ('Koji agregati tipovi podataka postoje u programskom jeziku C?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Lista i rječnik', FALSE),
    (currval('question_question_id_seq'), 'Polje (array) i struktura (struct)', TRUE),
    (currval('question_question_id_seq'), 'Stog i red', FALSE),
    (currval('question_question_id_seq'), 'Tuple i set', FALSE);

-- Pitanje 23
INSERT INTO question (question) VALUES ('Kako se u C-u pristupi elementu polja s indeksom i pomoću pokazivača p koji pokazuje na prvi element?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'p[i] ili *(p + i)', TRUE),
    (currval('question_question_id_seq'), 'p->i', FALSE),
    (currval('question_question_id_seq'), '(*p).i', FALSE),
    (currval('question_question_id_seq'), 'p.element(i)', FALSE);

-- Pitanje 24
INSERT INTO question (question) VALUES ('Što je rezultat oduzimanja dva pokazivača na isti tip u C-u?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Novi pokazivač na sredinu između njih', FALSE),
    (currval('question_question_id_seq'), 'Broj bajtova između dvije adrese', FALSE),
    (currval('question_question_id_seq'), 'Broj objekata referenciranog tipa između dvije adrese', TRUE),
    (currval('question_question_id_seq'), 'Uvijek nula', FALSE);

-- Pitanje 25
INSERT INTO question (question) VALUES ('Što je memorijski razred (storage class) varijable?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Veličina memorije koju varijabla zauzima', FALSE),
    (currval('question_question_id_seq'), 'Specifikacija koja određuje vidljivost i trajnost varijable', TRUE),
    (currval('question_question_id_seq'), 'Adresa varijable u radnoj memoriji', FALSE),
    (currval('question_question_id_seq'), 'Tip podatka koji varijabla pohranjuje', FALSE);

-- Pitanje 26
INSERT INTO question (question) VALUES ('Koliko traje statička varijabla definirana unutar funkcije?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Samo dok se funkcija izvršava', FALSE),
    (currval('question_question_id_seq'), 'Od trenutka definicije do kraja bloka', FALSE),
    (currval('question_question_id_seq'), 'Od početka do završetka izvršavanja cijelog programa', TRUE),
    (currval('question_question_id_seq'), 'Ovisi o tome je li inicijalizirana', FALSE);

-- Pitanje 27
INSERT INTO question (question) VALUES ('Na koju vrijednost se automatski inicijalizira statička varijabla koja nije eksplicitno inicijalizirana?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Na slučajnu vrijednost', FALSE),
    (currval('question_question_id_seq'), 'Na -1', FALSE),
    (currval('question_question_id_seq'), 'Na nulu', TRUE),
    (currval('question_question_id_seq'), 'Na NULL', FALSE);

-- Pitanje 28
INSERT INTO question (question) VALUES ('Što je eksterna varijabla (extern) u C-u?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Varijabla definirana unutar funkcije main', FALSE),
    (currval('question_question_id_seq'), 'Varijabla definirana izvan svih blokova, bez specifikacije static', TRUE),
    (currval('question_question_id_seq'), 'Varijabla koja se učitava iz vanjske datoteke', FALSE),
    (currval('question_question_id_seq'), 'Varijabla deklarirana s ključnom riječi extern unutar bloka', FALSE);

-- Pitanje 29
INSERT INTO question (question) VALUES ('Što je makro u programskom jeziku C?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Posebna vrsta funkcije bez parametara', FALSE),
    (currval('question_question_id_seq'), 'Pravilo za transformaciju teksta izvornog koda koje obavlja pretprocesor', TRUE),
    (currval('question_question_id_seq'), 'Varijabla čija se vrijednost ne može mijenjati', FALSE),
    (currval('question_question_id_seq'), 'Blok koda koji se može pozivati kao funkcija', FALSE);

-- Pitanje 30
INSERT INTO question (question) VALUES ('Kojom direktivom se definira makro u C-u?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), '#include', FALSE),
    (currval('question_question_id_seq'), '#macro', FALSE),
    (currval('question_question_id_seq'), '#define', TRUE),
    (currval('question_question_id_seq'), '#pragma', FALSE);

-- Pitanje 31
INSERT INTO question (question) VALUES ('Smije li direktiva pretprocesoru #define završavati znakom ";"?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Da, uvijek mora završavati s ";"', FALSE),
    (currval('question_question_id_seq'), 'Ne, direktiva pretprocesoru ne smije se terminirati znakom ";"', TRUE),
    (currval('question_question_id_seq'), 'Samo kada definiramo konstantu', FALSE),
    (currval('question_question_id_seq'), 'Ovisi o prevodiocu', FALSE);

-- Pitanje 32
INSERT INTO question (question) VALUES ('Koja je konvencija za imenovanje makro identifikatora u C-u?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Identifikator se piše malim slovima', FALSE),
    (currval('question_question_id_seq'), 'Identifikator se piše velikim slovima', TRUE),
    (currval('question_question_id_seq'), 'Identifikator mora počinjati podvlakom', FALSE),
    (currval('question_question_id_seq'), 'Nema posebne konvencije', FALSE);

-- Pitanje 33
INSERT INTO question (question) VALUES ('Što je standardna biblioteka programskog jezika C?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Skup programa koji se isporučuju uz prevodilac', FALSE),
    (currval('question_question_id_seq'), 'Skup funkcija, makro definicija i tipova dostupan na svim platformama', TRUE),
    (currval('question_question_id_seq'), 'Biblioteka funkcija specifičnih za Windows operacijski sustav', FALSE),
    (currval('question_question_id_seq'), 'Kolekcija primjera programskog koda', FALSE);

-- Pitanje 34
INSERT INTO question (question) VALUES ('Koja datoteka zaglavlja se mora uključiti za korištenje funkcija printf i scanf?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), '<stdlib.h>', FALSE),
    (currval('question_question_id_seq'), '<string.h>', FALSE),
    (currval('question_question_id_seq'), '<stdio.h>', TRUE),
    (currval('question_question_id_seq'), '<math.h>', FALSE);

-- Pitanje 35
INSERT INTO question (question) VALUES ('Koliko datoteka zaglavlja sadrži standardna biblioteka programskog jezika C (ISO C)?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), '10', FALSE),
    (currval('question_question_id_seq'), '29', TRUE),
    (currval('question_question_id_seq'), '50', FALSE),
    (currval('question_question_id_seq'), '7', FALSE);

-- Pitanje 36
INSERT INTO question (question) VALUES ('Što funkcija fopen vraća ako otvaranje datoteke nije uspjelo?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), '-1', FALSE),
    (currval('question_question_id_seq'), 'EOF', FALSE),
    (currval('question_question_id_seq'), 'NULL', TRUE),
    (currval('question_question_id_seq'), '0', FALSE);

-- Pitanje 37
INSERT INTO question (question) VALUES ('Koji modalitet otvaranja datoteke fopen omogućuje SAMO čitanje?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), '"w"', FALSE),
    (currval('question_question_id_seq'), '"a"', FALSE),
    (currval('question_question_id_seq'), '"r"', TRUE),
    (currval('question_question_id_seq'), '"rw"', FALSE);

-- Pitanje 38
INSERT INTO question (question) VALUES ('Što se dogodi s postojećim sadržajem datoteke kada se otvori s modalitetom "w"?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Sadržaj se čuva i novi podaci se dodaju na kraj', FALSE),
    (currval('question_question_id_seq'), 'Postojeći sadržaj se briše', TRUE),
    (currval('question_question_id_seq'), 'Sadržaj se kopira u sigurnosnu kopiju', FALSE),
    (currval('question_question_id_seq'), 'fopen vraća NULL ako datoteka već postoji', FALSE);

-- Pitanje 39
INSERT INTO question (question) VALUES ('Što opisuje "adresa objekta" u programskom jeziku C?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Ime varijable u izvornom kodu', FALSE),
    (currval('question_question_id_seq'), 'Adresa na kojoj je pohranjen prvi bajt sadržaja objekta', TRUE),
    (currval('question_question_id_seq'), 'Redni broj varijable u programu', FALSE),
    (currval('question_question_id_seq'), 'Veličina objekta u bajtovima', FALSE);

-- Pitanje 40
INSERT INTO question (question) VALUES ('Zašto adresa nije obični cijeli broj (int) u C-u?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Jer je uvijek negativna vrijednost', FALSE),
    (currval('question_question_id_seq'), 'Jer adresa u općem slučaju nije ni short, ni int, ni long', TRUE),
    (currval('question_question_id_seq'), 'Jer int ne može pohraniti velike brojeve', FALSE),
    (currval('question_question_id_seq'), 'Jer adrese uvijek završavaju decimalnim zarezom', FALSE);

-- Pitanje 41
INSERT INTO question (question) VALUES ('Što je strukturirano programiranje?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Programiranje u kojemu se sve varijable moraju definirati na početku', FALSE),
    (currval('question_question_id_seq'), 'Paradigma koja dozvoljava selekciju, iteraciju i poziv potprograma, a izbjegava goto', TRUE),
    (currval('question_question_id_seq'), 'Programiranje isključivo pomoću struktura (struct)', FALSE),
    (currval('question_question_id_seq'), 'Pisanje programa u više odvojenih datoteka', FALSE);

-- Pitanje 42
INSERT INTO question (question) VALUES ('Što je automatska varijabla (auto) u programskom jeziku C?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Varijabla čiji se tip automatski određuje iz vrijednosti', FALSE),
    (currval('question_question_id_seq'), 'Svaka varijabla definirana unutar bloka bez eksplicitnog memorijskog razreda', TRUE),
    (currval('question_question_id_seq'), 'Globalna varijabla dostupna u svim funkcijama', FALSE),
    (currval('question_question_id_seq'), 'Varijabla inicijalizirana na nulu', FALSE);

-- Pitanje 43
INSERT INTO question (question) VALUES ('Koji je opći oblik for petlje u C-u?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'for (uvjet) { tijelo; }', FALSE),
    (currval('question_question_id_seq'), 'for (izraz_1; izraz_2; izraz_3) naredba;', TRUE),
    (currval('question_question_id_seq'), 'for i in range { tijelo; }', FALSE),
    (currval('question_question_id_seq'), 'for (int i; i < n; ) { tijelo; }', FALSE);

-- Pitanje 44
INSERT INTO question (question) VALUES ('Što je realloc funkcija i kada se koristi?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Alocira novu memoriju i briše staru', FALSE),
    (currval('question_question_id_seq'), 'Mijenja veličinu već alociranog memorijskog bloka uz očuvanje podataka', TRUE),
    (currval('question_question_id_seq'), 'Resetira sadržaj alociranog bloka na nulu', FALSE),
    (currval('question_question_id_seq'), 'Alocira memoriju za polje struktura', FALSE);

-- Pitanje 45
INSERT INTO question (question) VALUES ('Kako se definira makro s parametrima u C-u?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), '#define IME [param] lista_zamjene', FALSE),
    (currval('question_question_id_seq'), '#define IME(p1, p2) lista_zamjene', TRUE),
    (currval('question_question_id_seq'), '#macro IME(p1, p2) lista_zamjene', FALSE),
    (currval('question_question_id_seq'), '#define IME param: lista_zamjene', FALSE);

-- Pitanje 46
INSERT INTO question (question) VALUES ('Zašto se svaki parametar u makrou s parametrima treba zasebno uokviriti zagradama?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Zbog bolje čitljivosti koda', FALSE),
    (currval('question_question_id_seq'), 'Da bi se izbjeglo pogrešno grupiranje pri zamjeni izraza umjesto jednostavnih varijabli', TRUE),
    (currval('question_question_id_seq'), 'Jer preprocesor ne prepoznaje parametre bez zagrada', FALSE),
    (currval('question_question_id_seq'), 'To nije potrebno, zagrade su opcionalne', FALSE);

-- Pitanje 47
INSERT INTO question (question) VALUES ('Što su lokalne varijable funkcije i koliko traju?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Vidljive su svugdje u programu i traju cijelo izvršavanje', FALSE),
    (currval('question_question_id_seq'), 'Vidljive su samo unutar funkcije i uklanjaju se završetkom izvršavanja funkcije', TRUE),
    (currval('question_question_id_seq'), 'Vidljive su u cijelom modulu i traju dok se modul ne unload-a', FALSE),
    (currval('question_question_id_seq'), 'Ovisi o tipu podatka koji pohranjuju', FALSE);

-- Pitanje 48
INSERT INTO question (question) VALUES ('Koji modalitet otvaranja datoteke treba koristiti za dodavanje podataka na kraj postojeće datoteke?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), '"w"', FALSE),
    (currval('question_question_id_seq'), '"r+"', FALSE),
    (currval('question_question_id_seq'), '"a"', TRUE),
    (currval('question_question_id_seq'), '"w+"', FALSE);

-- Pitanje 49
INSERT INTO question (question) VALUES ('Koja je razlika između tekstnih i binarnih datoteka u C-u?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Tekstne datoteke su uvijek veće od binarnih', FALSE),
    (currval('question_question_id_seq'), 'Binarne datoteke se otvaraju dodavanjem oznake "b" modalitetu (npr. "rb", "wb")', TRUE),
    (currval('question_question_id_seq'), 'Tekstne datoteke se ne mogu čitati u C programima', FALSE),
    (currval('question_question_id_seq'), 'Nema razlike, to su sinonimi', FALSE);

-- Pitanje 50
INSERT INTO question (question) VALUES ('Što se događa s memorijom pri beskonačnoj rekurziji u C-u?');
INSERT INTO answer (question_id, content, is_correct) VALUES
    (currval('question_question_id_seq'), 'Memorija na gomili (heap) se postepeno puni', FALSE),
    (currval('question_question_id_seq'), 'Stog (stack) se puni i dolazi do greške pri izvršavanju programa', TRUE),
    (currval('question_question_id_seq'), 'Program se sam zaustavlja bez greške', FALSE),
    (currval('question_question_id_seq'), 'Operacijski sustav automatski ograničava dubinu rekurzije', FALSE);
