package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.item.SaveCheck;
import hello.itemservice.domain.item.UpdateCheck;
import hello.itemservice.web.validation.form.ItemSaveForm;
import hello.itemservice.web.validation.form.ItemUpdateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/validation/v4/items")
@RequiredArgsConstructor
@Slf4j
public class ValidationItemControllerV4 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v4/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v4/addForm";
    }

    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute("item") ItemSaveForm form,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {

        if (null != form.getPrice() && null != form.getQuantity()) {
            int resultPrice = form.getPrice() * form.getQuantity();
            bindingResult.reject("totalPriceMin", new Object[]{20000}, "가격 * 수량 합은 10000원 이상 필요, 현재 값 =" + resultPrice);
        }

        // 검증 실패시 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("error={}", bindingResult);
            return "validation/v4/addForm";
        }

        // 객체 생성 및 변환
        Item item = new Item(form.getItemName(), form.getPrice(), form.getQuantity());

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v4/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @Validated @ModelAttribute("item") ItemUpdateForm form, BindingResult bindingResult) {

        if (null != form.getPrice() && null != form.getQuantity()) {
            int resultPrice = form.getPrice() * form.getQuantity();
            bindingResult.reject("totalPriceMin", new Object[]{20000}, "가격 * 수량 합은 10000원 이상 필요, 현재 값 =" + resultPrice);
        }

        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "validation/v4/editForm";
        }

        // 객체 생성 및 변환

        Item item = new Item(form.getItemName(), form.getPrice(), form.getQuantity());

        itemRepository.update(itemId, item);
        return "redirect:/validation/v4/items/{itemId}";
    }

}

