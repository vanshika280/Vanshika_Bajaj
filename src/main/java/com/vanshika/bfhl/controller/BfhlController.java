package com.vanshika.bfhl.controller;

import com.vanshika.bfhl.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/bfhl")
public class BfhlController {

    private static final String EMAIL = "vanshika2171.be23@chitkara.edu.in";

    @Autowired
    private GeminiService geminiService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> process(
            @RequestBody Map<String, Object> body) {

        LinkedHashMap<String, Object> res = new LinkedHashMap<>();
        res.put("is_success", true);
        res.put("official_email", EMAIL);

        try {

            Object data;

            // -------- Fibonacci --------
            if (body.containsKey("fibonacci")) {

                int n = ((Number) body.get("fibonacci")).intValue();
                List<Integer> fib = new ArrayList<>();

                int a = 0, b = 1;
                for (int i = 0; i < n; i++) {
                    fib.add(a);
                    int c = a + b;
                    a = b;
                    b = c;
                }
                data = fib;
            }

            // -------- Prime --------
            else if (body.containsKey("prime")) {

                List<?> arr = (List<?>) body.get("prime");
                List<Integer> primes = new ArrayList<>();

                for (Object obj : arr) {
                    int num = ((Number) obj).intValue();
                    if (num < 2) continue;

                    boolean isPrime = true;
                    for (int i = 2; i * i <= num; i++) {
                        if (num % i == 0) {
                            isPrime = false;
                            break;
                        }
                    }
                    if (isPrime) primes.add(num);
                }
                data = primes;
            }

            // -------- LCM --------
            else if (body.containsKey("lcm")) {

                List<?> arr = (List<?>) body.get("lcm");
                int lcm = ((Number) arr.get(0)).intValue();

                for (int i = 1; i < arr.size(); i++) {
                    int num = ((Number) arr.get(i)).intValue();
                    lcm = (lcm * num) / gcd(lcm, num);
                }
                data = lcm;
            }

            // -------- HCF --------
            else if (body.containsKey("hcf")) {

                List<?> arr = (List<?>) body.get("hcf");
                int hcf = ((Number) arr.get(0)).intValue();

                for (int i = 1; i < arr.size(); i++) {
                    int num = ((Number) arr.get(i)).intValue();
                    hcf = gcd(hcf, num);
                }
                data = hcf;
            }

            // 🔥 DEFAULT → AI (NO INVALID KEY)
            else {
                String question = body.values().iterator().next().toString();
                data = geminiService.ask(question);
            }

            res.put("data", data);
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            return error("Invalid input");
        }
    }

    // -------- GCD Helper --------
    private int gcd(int a, int b) {
        while (b != 0) {
            int t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

    // -------- Error Response --------
    private ResponseEntity<Map<String, Object>> error(String msg) {
        LinkedHashMap<String, Object> res = new LinkedHashMap<>();
        res.put("is_success", false);
        res.put("official_email", EMAIL);
        res.put("error", msg);
        return ResponseEntity.badRequest().body(res);
    }
}
