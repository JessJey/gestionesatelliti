package it.prova.gestionesatelliti.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.prova.gestionesatelliti.model.Satellite;
import it.prova.gestionesatelliti.model.StatoSatellite;
import it.prova.gestionesatelliti.repository.SatelliteRepository;

@Service
public class SatelliteServiceImpl implements SatelliteService {

	@Autowired
	private SatelliteRepository satelliteRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@Transactional(readOnly = true)
	public List<Satellite> listAllElements() {
		return (List<Satellite>) satelliteRepository.findAll();

	}

	@Override
	@Transactional(readOnly = true)
	public Satellite caricaSingoloElemento(Long id) {
		return satelliteRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void aggiorna(Satellite satelliteInstance) {
		satelliteRepository.save(satelliteInstance);
	}

	@Override
	@Transactional
	public void inserisciNuovo(Satellite satelliteInstance) {
		satelliteRepository.save(satelliteInstance);

	}

	@Override
	@Transactional
	public void rimuovi(Satellite satelliteInstance) {
		satelliteRepository.delete(satelliteInstance);

	}

	@Override
	@Transactional(readOnly = true)
	public List<Satellite> findByExample(Satellite example) {
		Map<String, Object> paramaterMap = new HashMap<String, Object>();
		List<String> whereClauses = new ArrayList<String>();

		StringBuilder queryBuilder = new StringBuilder("select s from Satellite s where s.id = s.id ");

		if (StringUtils.isNotEmpty(example.getDenominazione())) {
			whereClauses.add(" s.denominazione  like :denominazione ");
			paramaterMap.put("denominazione", "%" + example.getDenominazione() + "%");
		}
		if (example.getCodice() != null) {
			whereClauses.add("s.codice >= :codice ");
			paramaterMap.put("codice", example.getCodice());
		}
		if (example.getDataLancio() != null) {
			whereClauses.add("s.dataLancio >= :dataLancio ");
			paramaterMap.put("dataLancio", example.getDataLancio());
		}
		if (example.getStato() != null) {
			whereClauses.add(" s.stato =:stato ");
			paramaterMap.put("stato", example.getStato());
		}

		queryBuilder.append(!whereClauses.isEmpty() ? " and " : "");
		queryBuilder.append(StringUtils.join(whereClauses, " and "));
		TypedQuery<Satellite> typedQuery = entityManager.createQuery(queryBuilder.toString(), Satellite.class);

		for (String key : paramaterMap.keySet()) {
			typedQuery.setParameter(key, paramaterMap.get(key));
		}

		return typedQuery.getResultList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Satellite> findByLanciatiPiu2anniNoDisatt() {
		return satelliteRepository.findByDataLancioAndStatoNodisatt();
	}

	@Override
	public List<Satellite> findAllDisattivatiMaNonRientrati(StatoSatellite stato) {
		return satelliteRepository.findAllByStatoLikeAndDataRientroIsNull(stato);
	}

	@Override
	public List<Satellite> findAllByStatoFisso() {
		Date date = new Date();
		date.setYear(date.getYear()-10);
		return satelliteRepository.findAllByDataLancioLessThanAndStatoEquals( date, StatoSatellite.FISSO);
	}
}
