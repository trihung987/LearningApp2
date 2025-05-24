package me.trihung.learningapp2.Entity.dto;

import java.util.List;

public class AccuracyResponse {
    private String real_transcript;
    private String ipa_transcript;
    private String pronunciation_accuracy;
    private String real_transcripts;
    private String matched_transcripts;
    private String real_transcripts_ipa;
    private String matched_transcripts_ipa;
    private String pair_accuracy_category;
    private String start_time;
    private String end_time;
    private String is_letter_correct_all_words;

    // Getters and setters with proper naming
    public String getRealTranscript() {
        return real_transcript;
    }

    public void setRealTranscript(String real_transcript) {
        this.real_transcript = real_transcript;
    }

    public String getIpaTranscript() {
        return ipa_transcript;
    }

    public void setIpaTranscript(String ipa_transcript) {
        this.ipa_transcript = ipa_transcript;
    }

    public String getPronunciationAccuracy() {
        return pronunciation_accuracy;
    }

    public void setPronunciationAccuracy(String pronunciation_accuracy) {
        this.pronunciation_accuracy = pronunciation_accuracy;
    }

    public String getRealTranscripts() {
        return real_transcripts;
    }

    public void setRealTranscripts(String real_transcripts) {
        this.real_transcripts = real_transcripts;
    }

    public String getMatchedTranscripts() {
        return matched_transcripts;
    }

    public void setMatchedTranscripts(String matched_transcripts) {
        this.matched_transcripts = matched_transcripts;
    }

    public String getRealTranscriptsIpa() {
        return real_transcripts_ipa;
    }

    public void setRealTranscriptsIpa(String real_transcripts_ipa) {
        this.real_transcripts_ipa = real_transcripts_ipa;
    }

    public String getMatchedTranscriptsIpa() {
        return matched_transcripts_ipa;
    }

    public void setMatchedTranscriptsIpa(String matched_transcripts_ipa) {
        this.matched_transcripts_ipa = matched_transcripts_ipa;
    }

    public String getPairAccuracyCategory() {
        return pair_accuracy_category;
    }

    public void setPairAccuracyCategory(String pair_accuracy_category) {
        this.pair_accuracy_category = pair_accuracy_category;
    }

    public String getStartTime() {
        return start_time;
    }

    public void setStartTime(String start_time) {
        this.start_time = start_time;
    }

    public String getEndTime() {
        return end_time;
    }

    public void setEndTime(String end_time) {
        this.end_time = end_time;
    }

    public String getIsLetterCorrectAllWords() {
        return is_letter_correct_all_words;
    }

    public void setIsLetterCorrectAllWords(String is_letter_correct_all_words) {
        this.is_letter_correct_all_words = is_letter_correct_all_words;
    }
}