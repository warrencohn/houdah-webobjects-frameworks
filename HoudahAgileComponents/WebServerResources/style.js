$j("document").ready(function() {
	loadBehaviors();
});


function loadBehaviors()
{
	$j("input.quickSearchLoupe").removeAttr("value");
	$j("input.quickSearchLoupe").addClass("quickSearchIcon");

	$j("input.quickSearchLoupe").click(function()
	{
		return quickSearchButton($(this));
	});

	$j("input.quickSearchAction").click(function()
	{
		return quickSearchButton($(this));
	});
	$j(".list .toolbar a.add").click(function()
	{
		return quickSearchLink($(this));
	});
}

function reloadPage()
{
	window.location.reload();
}

function quickSearchButton(element)
{
	var form = element.form;
	var target = form.action + '?';
	
	target += $j.param($j(form).formToArray(true));
	target += "&" + encodeURIComponent(element.name) + "=true"

	Modalbox.show(target, { overlayOpacity: 0.4, title: "", afterLoad: loadBehaviors, afterHide: reloadPage });
			
	return false;
}

function quickSearchLink(element)
{
	var target = element.href;

	Modalbox.show(target, { overlayOpacity: 0.4, title: "", afterLoad: loadBehaviors, afterHide: reloadPage });
			
	return false;
}