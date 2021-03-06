package it.prova.gestionesatelliti.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.prova.gestionesatelliti.model.Satellite;
import it.prova.gestionesatelliti.model.StatoSatellite;
import it.prova.gestionesatelliti.service.SatelliteService;

@Controller
@RequestMapping(value = "/satellite")
public class SatelliteController {

	@Autowired
	private SatelliteService satelliteService;

	@GetMapping
	public ModelAndView listAll() {
		ModelAndView mv = new ModelAndView();
		List<Satellite> results = satelliteService.listAllElements();
		mv.addObject("satellite_list_attribute", results);
		mv.setViewName("satellite/list");
		return mv;
	}

	@GetMapping("/search")
	public String search() {
		return "satellite/search";
	}

	@PostMapping("/list")
	public String listByExample(Satellite example, ModelMap model) {
		List<Satellite> results = satelliteService.findByExample(example);
		model.addAttribute("satellite_list_attribute", results);
		return "satellite/list";
	}

	@GetMapping("/insert")
	public String create(Model model) {
		model.addAttribute("insert_satellite_attr", new Satellite());
		return "satellite/insert";
	}

	@PostMapping("/save")
	public String save(@Valid @ModelAttribute("insert_satellite_attr") Satellite satellite, BindingResult result,
			RedirectAttributes redirectAttrs) {

		if (result.hasErrors())
			return "satellite/insert";

		satelliteService.inserisciNuovo(satellite);

		redirectAttrs.addFlashAttribute("successMessage", "Operazione eseguita correttamente");
		return "redirect:/satellite";
	}

	@GetMapping("/show/{idSatellite}")
	public String show(@PathVariable(required = true) Long idSatellite, Model model) {
		model.addAttribute("show_satellite_attr", satelliteService.caricaSingoloElemento(idSatellite));
		return "satellite/show";
	}

	@GetMapping("/modifica/{idSatellite}")
	public String modifica(@PathVariable(required = true) Long idSatellite, Model model) {
		model.addAttribute("modifica_satellite_attr", satelliteService.caricaSingoloElemento(idSatellite));
		return "satellite/modifica";
	}

	@PostMapping("/salvamodifica")
	public String salvamodifica(@ModelAttribute("modifica_satellite_attr") Satellite satellite, ModelMap model,
			RedirectAttributes redirectAttrs, BindingResult result) {

		if (result.hasErrors())
			return "satellite/modifica";

		satelliteService.aggiorna(satellite);

		redirectAttrs.addFlashAttribute("successMessage", "Operazione eseguita correttamente");
		return "redirect:/satellite";

	}
	
	@GetMapping("/delete/{idSatellite}")
	public String delete(@PathVariable(required = true) Long idSatellite, Model model) {
		model.addAttribute("delete_satellite_attr", satelliteService.caricaSingoloElemento(idSatellite));
		return "satellite/delete";
	}

	@GetMapping("/salvadelete/{idSatellite}")
	public String salvadelete(@PathVariable(required = true) Long idSatellite, ModelMap model,
			RedirectAttributes redirectAttrs) {

		Satellite satDaRim = satelliteService.caricaSingoloElemento(idSatellite);
		satelliteService.rimuovi(satDaRim);

		redirectAttrs.addFlashAttribute("successMessage", "Operazione eseguita correttamente");
		return "redirect:/satellite";
	}
	
	@PostMapping("/searchdatalancionodisatt")
	public String searchdatalancionodisatt(ModelMap model) {
		List<Satellite> results = satelliteService.findByLanciatiPiu2anniNoDisatt();
		model.addAttribute("satellite_list_attribute", results);
		return "satellite/list";
	}
	
	@PostMapping("/searchdisattmanonrientrati")
	public String searchdisattmanonrientrati(ModelMap model) {
		List<Satellite> results = satelliteService.findAllDisattivatiMaNonRientrati(StatoSatellite.DISATTIVATO);
		model.addAttribute("satellite_list_attribute", results);
		return "satellite/list";
	}
	
	@PostMapping("/searchdieciorbitafisso")
	public String searchdieciorbitafisso(ModelMap model) {
		List<Satellite> results = satelliteService.findAllByStatoFisso();
		model.addAttribute("satellite_list_attribute", results);
		return "satellite/list";
	}

}
