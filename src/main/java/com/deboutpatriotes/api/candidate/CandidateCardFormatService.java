package com.deboutpatriotes.api.candidate;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deboutpatriotes.api.candidate.CandidateDtos.CardFormats;
import com.deboutpatriotes.api.candidate.CandidateDtos.Ratio;

/** Proportion des photos des cartes candidats, une par emplacement du site. */
@Service
@Transactional(readOnly = true)
public class CandidateCardFormatService {

    static final String HOME = "home";
    static final String LIST = "list";

    /** Proportion retenue tant qu'aucune n'a été enregistrée : le portrait 4/5 d'origine. */
    private static final Ratio DEFAULT_RATIO = new Ratio(4, 5);

    private final CandidateCardFormatRepository formats;

    CandidateCardFormatService(CandidateCardFormatRepository formats) {
        this.formats = formats;
    }

    public CardFormats get() {
        Map<String, CandidateCardFormat> saved = formats.findAll().stream()
                .collect(Collectors.toMap(CandidateCardFormat::getPlacement, Function.identity()));
        return new CardFormats(ratio(saved.get(HOME)), ratio(saved.get(LIST)));
    }

    @Transactional
    public CardFormats update(CardFormats request) {
        save(HOME, request.home());
        save(LIST, request.list());
        return request;
    }

    private void save(String placement, Ratio ratio) {
        CandidateCardFormat format = formats.findById(placement).orElseGet(() -> new CandidateCardFormat(placement));
        format.setRatioWidth(ratio.width());
        format.setRatioHeight(ratio.height());
        formats.save(format);
    }

    private static Ratio ratio(CandidateCardFormat format) {
        return format == null ? DEFAULT_RATIO : new Ratio(format.getRatioWidth(), format.getRatioHeight());
    }
}
