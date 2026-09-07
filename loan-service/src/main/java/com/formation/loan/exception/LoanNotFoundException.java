package com.formation.loan.exception; public class LoanNotFoundException extends RuntimeException {public LoanNotFoundException(Long id){super("L'emprunt "+id+" n'existe pas");}}
