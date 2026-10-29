package com.example.homestay.web;

import com.example.homestay.domain.Homestay;
import com.example.homestay.domain.Room;
import com.example.homestay.repository.HomestayRepository;
import com.example.homestay.repository.RoomRepository;
import com.example.homestay.service.AvailabilityService;
import com.example.homestay.web.form.HomestayForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class HomestayController {

    private final HomestayRepository homestayRepository;
    private final RoomRepository roomRepository;
    private final AvailabilityService availabilityService;

    public HomestayController(
            HomestayRepository homestayRepository,
            RoomRepository roomRepository,
            AvailabilityService availabilityService) {
        this.homestayRepository = homestayRepository;
        this.roomRepository = roomRepository;
        this.availabilityService = availabilityService;
    }

    @GetMapping("/homestays")
    public String list(@RequestParam(required = false) String q, Model model) {
        List<Homestay> homestays = q == null || q.isBlank()
                ? homestayRepository.findAllByActiveTrueOrderByCreatedAtDesc()
                : homestayRepository.findByNameContainingIgnoreCaseOrCityContainingIgnoreCaseOrderByCreatedAtDesc(q, q);
        List<HomestayCard> cards = homestays.stream()
                .map(h -> new HomestayCard(h, roomRepository.sumTotalRoomsByHomestayId(h.getId())))
                .toList();
        model.addAttribute("cards", cards);
        model.addAttribute("query", q == null ? "" : q);
        return "homestays";
    }

    @GetMapping("/homestays/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Homestay homestay = homestayRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("门店不存在"));
        LocalDate today = LocalDate.now();
        List<RoomAvailabilityView> rooms = roomRepository.findByHomestayIdOrderByPriceAsc(id).stream()
                .map(room -> new RoomAvailabilityView(
                        room, availabilityService.availableRooms(room, today, today.plusDays(1))))
                .toList();
        model.addAttribute("homestay", homestay);
        model.addAttribute("rooms", rooms);
        return "homestay-detail";
    }

    @GetMapping("/homestays/new")
    public String createPage(Model model) {
        model.addAttribute("homestayForm", new HomestayForm());
        model.addAttribute("editing", false);
        return "homestay-form";
    }

    @GetMapping("/homestays/{id}/edit")
    public String editPage(@PathVariable Long id, Model model) {
        Homestay homestay = homestayRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("门店不存在"));
        HomestayForm form = new HomestayForm();
        form.setId(homestay.getId());
        form.setName(homestay.getName());
        form.setCity(homestay.getCity());
        form.setAddress(homestay.getAddress());
        form.setSummary(homestay.getSummary());
        form.setDescription(homestay.getDescription());
        form.setCoverImage(homestay.getCoverImage());
        form.setTags(homestay.getTags());
        form.setActive(homestay.isActive());
        model.addAttribute("homestayForm", form);
        model.addAttribute("editing", true);
        return "homestay-form";
    }

    @PostMapping("/homestays")
    public String save(
            @Valid @ModelAttribute("homestayForm") HomestayForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("editing", form.getId() != null);
            return "homestay-form";
        }

        Homestay homestay = form.getId() == null
                ? new Homestay()
                : homestayRepository.findById(form.getId())
                        .orElseThrow(() -> new IllegalArgumentException("门店不存在"));
        homestay.setName(form.getName().trim());
        homestay.setCity(form.getCity().trim());
        homestay.setAddress(form.getAddress().trim());
        homestay.setSummary(form.getSummary().trim());
        homestay.setDescription(form.getDescription());
        homestay.setCoverImage(form.getCoverImage().trim());
        homestay.setTags(form.getTags());
        homestay.setActive(form.isActive());
        homestayRepository.save(homestay);

        redirectAttributes.addFlashAttribute("successMessage",
                form.getId() == null ? "门店已创建" : "门店信息已更新");
        return "redirect:/homestays/" + homestay.getId();
    }

    public record HomestayCard(Homestay homestay, long totalRooms) {
    }

    public record RoomAvailabilityView(Room room, int availableRooms) {
    }
}
