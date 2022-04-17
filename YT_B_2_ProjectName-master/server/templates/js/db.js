var dialogInvalidData;

$(document).ready(function (){
	// listener for checking invalid data
	dialogInvalidData = document.querySelector('#dialog-invalid-data');
	if (!dialogInvalidData.showModal) {
		dialogPolyfill.registerDialog(dialogInvalidData);
	}
	dialogInvalidData.querySelector('.close').addEventListener('click', function() {
		dialogInvalidData.close();
	});

	// add listeners to each checkbox

	if (document.querySelector('table') !== null && table.querySelector('thead .mdl-data-table__select input') !== null) {
		var table = document.querySelector('table');
		var headerCheckbox = table.querySelector('thead .mdl-data-table__select input');
		var boxes = table.querySelectorAll('tbody .mdl-data-table__select');
		var deleteMultipleButton = document.querySelector('#delete-multiple-button');

		var numChecked = 0;
		headerCheckbox.addEventListener('change', function(event) {
			if (event.target.checked) {
				numChecked = boxes.length;
				deleteMultipleButton.disabled = false;

				for (var i = 0, length = boxes.length; i < length; i++) {
					boxes[i].MaterialCheckbox.check();
				}
			} else {
				numChecked = 0;
				deleteMultipleButton.disabled = true;

				for (var i = 0, length = boxes.length; i < length; i++) {
					boxes[i].MaterialCheckbox.uncheck();
				}
			}
		});

		// only disable deleteMultipleButton all boxes are unchecked
		for (var i = 0, length = boxes.length; i < length; i++) {
			boxes[i].addEventListener('change', function(event){
				if (event.target.checked) {
					numChecked++;
					if (numChecked > 0) deleteMultipleButton.disabled = false;
				} else {
					numChecked--;
					if (numChecked == 0) deleteMultipleButton.disabled = true;
				}
			});
		}
	}
});

function addTest(){
	var FirstName = $('#dFirstName').val();
	var LastName = $('#dLastName').val();

	if (FirstName === "" || LastName === "") {
		dialogInvalidData.showModal();
	} else {
		$.ajax({
			type: 'POST',
			url: "/api/v1/tests/",
			data: {
				"firstname": FirstName,
				"lastname": LastName
			},
			success: function (data) {
				console.log(data);
				window.location.reload();
			},
			error: function (data) {
				console.error("Did not add!");
			}
		});
	}
}

function runItem(request) {

	var ItemName = $('#dItemName').val();
	var OwnerID = $('#dOwnerID').val();
	var ItemsHouseID = $('#dItemsHouseID').val();
	var ItemID = $('#dItemID').val();
	var BorrowerID = $('#dBorrowerID').val();
	var DateBorrowed = $('#dDateBorrowed').val();
	var DateDue = $('#dDateDue').val();
	var Available = $('#dAvailable').val();

	if (request === 'DELETE' && ItemID !== "") {
		$.ajax({
			type: request,
			url: "/api/items/" + ItemID,
			dataType: "json",
			success: function (msg) {
				if (msg) {
					console.log(data);
					window.location.reload();
				} else {
					console.error("Did not complete!");
				}
			}
			,data: sendInfo
		})
	} else if (ItemName === "" || ItemsHouseID === "") {
		dialogInvalidData.showModal();
	} else {
		var sendInfo = {
			ItemName: ItemName,
			OwnerID: OwnerID,
			ItemsHouseID: ItemsHouseID,
			ItemID: ItemID,
			BorrowerID: BorrowerID,
			DateBorrowed: DateBorrowed,
			DateDue: DateDue,
			Available: Available,
		}
		console.log(request);
		$.ajax({
			type: request,
			url: "/api/items/",
			dataType: "json",
			success: function (msg) {
				if(msg) {
					console.log(data);
					window.location.reload();
				}else{
					console.error("Did not complete!");
				}
			},
			data: sendInfo
		});
	}
	location.reload;
}

function addItem(){
	runItem("POST");
}


function updateItem(){
	runItem("PATCH");
}


function removeItem(){
	runItem("DELETE");
}

function editID(id){
	var FirstName = $('#dFirstName').val();
	var LastName = $('#dLastName').val();

	if (FirstName === "" || LastName === "") {
		dialogInvalidData.showModal();
	} else {
		$.ajax({
			type: 'PUT',
			url: "/api/v1/tests/" + id,
			data: {
				"firstname": FirstName,
				"lastname": LastName
			},
			success: function (data) {
				console.log(data);
				window.location.reload();
			},
			error: function () {
				console.error("Did not edit " + id + "!");
			}
		});
	}
};

function deleteID(id){
	doDeleteID(id, true);
};

// helper function
function doDeleteID(id, reload){
	$.ajax({
		type: 'DELETE',
		async: false,
		timeout: 2000,
		url: "/api/v1/tests/" + id,
		success: function (data) {
			console.log(data);
			if (reload == true) window.location.reload();
		},
		error: function () {
			console.error("Did not delete " + id + "!");
		}
	});
};

function deleteMultiple(){
	dbtable = document.querySelector('#db-table');
	checked = dbtable.getElementsByClassName('is-checked');

	for (var i = 0; i < checked.length; i++) {
		doDeleteID(checked[i].parentElement.parentElement.id, false);
	}

	window.location.reload();
}