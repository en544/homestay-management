package com.example.homestay.web;

import com.example.homestay.domain.Homestay;
import com.example.homestay.domain.Room;
import com.example.homestay.domain.RoomStatus;
import com.example.homestay.repository.HomestayRepository;
import com.example.homestay.repository.RoomRepository;
import com.example.homestay.web.form.RoomForm;
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

@Controller
public class RoomController {

    private final RoomRepository roomRepository;
    private final HomestayRepository homestayRepository;

    public RoomController(RoomRepository roomRepository, HomestayRepository homestayRepository) {
        this.roomRepository = roomRepository;
        this.homestayRepository = homestayRepository;
    }

    @GetMapping("/rooms")
    public String list(Model model) {
        model.addAttribute("rooms", roomRepository.findAllByOrderByPriceAsc());
        model.addAttribute("statuses", RoomStatus.values());
        return "rooms";
    }

    @GetMapping("/rooms/new")
    public String createPage(Model model) {
        model.addAttribute("roomForm", new RoomForm());
        model.addAttribute("editing", false);
        populateHomestays(model);
        return "room-form";
    }

    @GetMapping("/rooms/{id}/edit")
    public String editPage(@PathVariable Long id, Model model) {
        Room room = roomRepository.findWithHomestayById(id)
                .orElseThrow(() -> new IllegalArgumentException("房型不存在"));
        RoomForm form = new RoomForm();
        form.setId(room.getId());
        form.setHomestayId(room.getHomestay().getId());
        form.setName(room.getName());
        form.setRoomType(room.getRoomType());
        form.setCapacity(room.getCapacity());
        form.setPrice(room.getPrice());
        form.setTotalRooms(room.getTotalRooms());
        form.setStatus(room.getStatus());
        form.setAmenities(room.getAmenities());
        form.setCoverImage(room.getCoverImage());
        model.addAttribute("roomForm", form);
        model.addAttribute("editing", true);
        populateHomestays(model);
        return "room-form";
    }

    @PostMapping("/rooms")
    public String save(
            @Valid @ModelAttribute("roomForm") RoomForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("editing", form.getId() != null);
            populateHomestays(model);
            return "room-form";
        }

        Homestay homestay = homestayRepository.findById(form.getHomestayId())
                .orElseThrow(() -> new IllegalArgumentException("门店不存在"));
        Room room = form.getId() == null
                ? new Room()
                : roomRepository.findById(form.getId())
                        .orElseThrow(() -> new IllegalArgumentException("房型不存在"));
        room.setHomestay(homestay);
        room.setName(form.getName().trim());
        room.setRoomType(form.getRoomType().trim());
        room.setCapacity(form.getCapacity());
        room.setPrice(form.getPrice());
        room.setTotalRooms(form.getTotalRooms());
        room.setStatus(form.getStatus());
        room.setAmenities(form.getAmenities());
        room.setCoverImage(form.getCoverImage() == null || form.getCoverImage().isBlank()
                ? homestay.getCoverImage()
                : form.getCoverImage().trim());
        roomRepository.save(room);

        redirectAttributes.addFlashAttribute("successMessage",
                form.getId() == null ? "房型已创建" : "房型信息已更新");
        return "redirect:/rooms";
    }

    @PostMapping("/rooms/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam RoomStatus status,
            RedirectAttributes redirectAttributes) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("房型不存在"));
        room.setStatus(status);
        roomRepository.save(room);
        redirectAttributes.addFlashAttribute("successMessage", "房态已更新为「" + status.getLabel() + "」");
        return "redirect:/rooms";
    }

    private void populateHomestays(Model model) {
        model.addAttribute("homestays", homestayRepository.findAllByActiveTrueOrderByCreatedAtDesc());
        model.addAttribute("roomStatuses", RoomStatus.values());
    }
}
