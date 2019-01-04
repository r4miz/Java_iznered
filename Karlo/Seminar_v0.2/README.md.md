# Spring User Interface

Ime baze: user_interface_db

# commit v0.2 @Karlo

riseno ->   navigacija je fluentna i jasna
            prikaz profila (ime,prezime,mail)
            
            edit profila    - (ime,prezime,password) pw se provjerava jel matcha, save ekriptiran
                            - email nisan implementira, moze se lako al korisnika goni onda na       login, problem kod spring securitya sta prati preko emaila ko je       logiran...
            
            delete profila  - sacuvaju se svi podaci jer se samo active seta u 0, pari ka da ga                      nema

# commit v0.2.1 @Karlo

riseno -> 	dodana fragmentacija html-ova, lakse se mogu vrsit izmjene nad navigacijskon trakon
			razdvojeno kompletno sucelje admina i usera, admini imaju sve sto i useri, ali dodatno toga ce bit njima
			USER dio je gotov osim racuna...
			

			dodani temelji administrativnih alata	-	prikaz svih korisnika
														editanje bilo kojeg
															-	davanje ADMIN ili micanje ADMIN prava nekome
															-	deakcivacija i reaktivacija accounta
													- 	treba omogucit tu i vodenje s racunima...