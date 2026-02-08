package services;

import model.User;
import repository.AccountRepository;

public class AuthenticationService {
	AccountRepository accountRepository = new AccountRepository();

	public User login(String hospitalId, String password) throws Exception {
		return accountRepository.login(hospitalId, password);
	}
}
