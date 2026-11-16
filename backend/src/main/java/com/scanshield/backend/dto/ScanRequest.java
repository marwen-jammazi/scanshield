package com.scanshield.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ScanRequest(@NotBlank(message = "Message must not be empty") 
@Size(max = 5000 , message = "Message is too long (max 5000 character)")
String message ) 

{}
