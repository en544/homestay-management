(function () {
    "use strict";

    if (window.lucide) {
        window.lucide.createIcons();
    }

    document.querySelectorAll("[data-confirm]").forEach(function (form) {
        form.addEventListener("submit", function (event) {
            var message = form.getAttribute("data-confirm");
            if (message && !window.confirm(message)) {
                event.preventDefault();
            }
        });
    });

    document.querySelectorAll("[data-auto-submit]").forEach(function (select) {
        select.addEventListener("change", function () {
            if (select.form) {
                select.form.submit();
            }
        });
    });

    var bookingForm = document.querySelector("[data-booking-form]");
    if (bookingForm) {
        var roomSelect = bookingForm.querySelector("[name='roomId']");
        var checkIn = bookingForm.querySelector("[name='checkIn']");
        var checkOut = bookingForm.querySelector("[name='checkOut']");
        var roomCount = bookingForm.querySelector("[name='roomCount']");
        var guestCount = bookingForm.querySelector("[name='guests']");

        var amountNode = document.querySelector("[data-booking-total]");
        var nightsNode = document.querySelector("[data-booking-nights]");
        var capacityNode = document.querySelector("[data-booking-capacity]");
        var availableNode = document.querySelector("[data-booking-available]");
        var summaryTitle = document.querySelector("[data-summary-title]");
        var summaryLocation = document.querySelector("[data-summary-location]");
        var summaryImage = document.querySelector("[data-summary-image]");

        function selectedOption() {
            return roomSelect && roomSelect.options[roomSelect.selectedIndex];
        }

        function updateGuestLimit() {
            var option = selectedOption();
            var count = Math.max(1, Number(roomCount && roomCount.value) || 1);
            var capacity = Number(option && option.dataset.capacity) || 2;
            var limit = capacity * count;
            if (guestCount) {
                guestCount.max = String(limit);
                if (Number(guestCount.value) > limit) {
                    guestCount.value = String(limit);
                }
            }
            if (capacityNode) {
                capacityNode.textContent = "每间最多 " + capacity + " 人";
            }
        }

        function updateSummary() {
            var option = selectedOption();
            if (!option) {
                return;
            }
            if (summaryTitle) {
                summaryTitle.textContent = option.dataset.roomName || option.text;
            }
            if (summaryLocation) {
                summaryLocation.textContent =
                    (option.dataset.homestay || "") + " · " + (option.dataset.city || "");
            }
            if (summaryImage && option.dataset.image) {
                summaryImage.src = option.dataset.image;
            }
            if (availableNode) {
                availableNode.textContent = (option.dataset.available || "0") + " 间可订";
            }
            updateGuestLimit();
        }

        function updateTotal() {
            var option = selectedOption();
            if (!option || !checkIn.value || !checkOut.value) {
                return;
            }
            var start = new Date(checkIn.value + "T00:00:00");
            var end = new Date(checkOut.value + "T00:00:00");
            var nights = Math.max(0, Math.round((end - start) / 86400000));
            var count = Math.max(1, Number(roomCount.value) || 1);
            var price = Number(option.dataset.price) || 0;
            if (nightsNode) {
                nightsNode.textContent = nights > 0 ? nights + " 晚" : "请选择日期";
            }
            if (amountNode) {
                amountNode.textContent = nights > 0
                    ? (price * count * nights).toLocaleString("zh-CN", {
                        minimumFractionDigits: 0,
                        maximumFractionDigits: 2
                    })
                    : "0";
            }
        }

        [roomSelect, checkIn, checkOut, roomCount].forEach(function (input) {
            if (input) {
                input.addEventListener("change", function () {
                    updateSummary();
                    updateTotal();
                });
            }
        });
        if (guestCount) {
            guestCount.addEventListener("change", updateGuestLimit);
        }

        if (checkOut && checkIn && !checkOut.value) {
            var tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);
            checkOut.value = tomorrow.toISOString().slice(0, 10);
        }
        updateSummary();
        updateTotal();
    }

    document.querySelectorAll(".alert[data-dismiss]").forEach(function (alert) {
        window.setTimeout(function () {
            alert.style.opacity = "0";
            alert.style.transform = "translateY(-4px)";
            window.setTimeout(function () {
                alert.remove();
            }, 220);
        }, 4500);
    });
})();
