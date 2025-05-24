package me.trihung.learningapp2.Entity.dto;

import java.util.List;

public class SampleResponse {
    private List<String> real_transcript;
    private String ipa_transcript;
    private String transcript_translation;

    public List<String> getRealTranscript() {
        return real_transcript;
    }

    public void setRealTranscript(List<String> real_transcript) {
        this.real_transcript = real_transcript;
    }

    public String getIpaTranscript() {
        return ipa_transcript;
    }

    public void setIpaTranscript(String ipa_transcript) {
        this.ipa_transcript = ipa_transcript;
    }

    public String getTranscriptTranslation() {
        return transcript_translation;
    }

    public void setTranscriptTranslation(String transcript_translation) {
        this.transcript_translation = transcript_translation;
    }
}