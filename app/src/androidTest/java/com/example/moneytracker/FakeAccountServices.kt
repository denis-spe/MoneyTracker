package com.example.moneytracker

import com.example.moneytracker.backend.auth.AccountServicesImpl
import com.google.firebase.auth.FirebaseAuth

class FakeAccountServices(auth: FirebaseAuth) : AccountServicesImpl(auth)