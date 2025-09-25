package com.climbup.service.productivity;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class MotivationService {

    private final List<String> quotes = List.of(
    		 "Believe in yourself and all that you are.",
    		    "Push yourself, because no one else is going to do it for you.",
    		    "You are stronger than you think.",
    		    "Don’t watch the clock; do what it does. Keep going.",
    		    "Discipline is the bridge between goals and accomplishment.",
    		    "Small steps every day lead to big results.",
    		    "Success is the sum of small efforts, repeated daily.",
    		    "It’s not about being the best. It’s about being better than you were yesterday.",
    		    "Winners are not people who never fail, but people who never quit.",
    		    "Focus on progress, not perfection.",
    		    "Hard work beats talent when talent doesn't work hard.",
    		    "A little progress each day adds up to big results.",
    		    "The secret of getting ahead is getting started.",
    		    "Great things never come from comfort zones.",
    		    "Don’t limit your challenges. Challenge your limits.",
    		    "You don’t have to be great to start, but you have to start to be great.",
    		    "Your future is created by what you do today, not tomorrow.",
    		    "The way to get started is to quit talking and begin doing.",
    		    "Productivity is never an accident. It is always the result of a commitment to excellence.",
    		    "Start where you are. Use what you have. Do what you can.",
    		    "What you do today can improve all your tomorrows.",
    		    "Motivation is what gets you started. Habit is what keeps you going.",
    		    "Don’t stop when you're tired. Stop when you're done.",
    		    "Wake up with determination. Go to bed with satisfaction.",
    		    "Success doesn’t come from what you do occasionally. It comes from what you do consistently.",
    		    "Dream it. Wish it. Do it.",
    		    "Be so good they can’t ignore you.",
    		    "Action is the foundational key to all success.",
    		    "A goal without a plan is just a wish.",
    		    "Success is not final, failure is not fatal: It is the courage to continue that counts.",
    		    "Don’t be busy, be productive.",
    		    "The expert in anything was once a beginner.",
    		    "Stay focused and never give up.",
    		    "Success starts with self-discipline.",
    		    "Make each day your masterpiece.",
    		    "Consistency is more important than perfection.",
    		    "The only bad workout is the one that didn’t happen.",
    		    "Work hard in silence, let success make the noise.",
    		    "Doubt kills more dreams than failure ever will.",
    		    "You miss 100% of the shots you don’t take.",
    		    "Strive for progress, not perfection.",
    		    "Set a goal that makes you want to jump out of bed in the morning.",
    		    "Winners focus on winning. Losers focus on winners.",
    		    "Energy and persistence conquer all things.",
    		    "Don’t count the days, make the days count.",
    		    "You don’t find willpower, you create it.",
    		    "The best way to predict your future is to create it.",
    		    "Success isn’t always about greatness. It’s about consistency.",
    		    "Stay hungry. Stay foolish.",
    		    "One day or day one — you decide."
    		);

    public String getRandomQuote() {
        Random random = new Random();
        return quotes.get(random.nextInt(quotes.size()));
    }
}
