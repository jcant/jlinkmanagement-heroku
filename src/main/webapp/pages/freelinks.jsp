<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>JLink Management</title>

<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css"
	integrity="sha384-WskhaSGFgHYWDcbwN70/dfYBj47jz9qbsMId/iRN3ewGhXQFZCSftd1LZCfmhktB"
	crossorigin="anonymous">

<script type="application/javascript" src="https://code.jquery.com/jquery-3.3.1.js" />

<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"
	integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49"
	crossorigin="anonymous"></script>

<script
	src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js"
	integrity="sha384-smHYKdLADwkXOn1EmN1qk/HfnUcbVRZyYmZ4qpPea6sjB/pTJ0euyQp0Mk8ck+5T"
	crossorigin="anonymous"></script>

<script src='https://www.google.com/recaptcha/api.js'></script>

<script src='js/freelinks.js'></script>
<script src='js/utils.js'></script>
<link href="css/main.css" rel="stylesheet">
</head>
<body>
	<%@include file="_header.jsp"%>

	<div>
		<div class="row">
			<div class="col-sm-2">
				<div class="card-header bg-warning">
					<input type="checkbox" id="showArchive">
					<label class="form-check-label" for="showArchive">Show Archive</label>
				</div>
			</div>
			<div class="col">
				<div class="container">
					<h5>Links statistics of <strong><span id="header_username">#none</span></strong></h5>
				</div>
				<div class="container" id="link_add">
					<div id = "message"></div>
					<form>
						<div class="form-row">
							<div class="form-group align-self-end col-md-3">
								<select id="rootLinks" class="form-control">
									<option selected>Problems with connection to REST service?</option>
								</select>
							</div>
							<div class="form-group col-md-3">
								<label for="target" >Target</label>
								<input type="text" class="form-control" id="target">
							</div>
							<div class="form-group align-self-end col-md-2">
								<button type="button" id="createButton" class="btn btn-primary">Create</button>
							</div>
						</div>
					</form>
				</div>
				<div class="container">
					<div class="table-responsive-lg">
						<table class="table table-hover">
							<thead className="thead-dark">
							<tr>
								<th scope="col">URL</th>
								<th scope="col">Target</th>
								<th scope="col">Start Date</th>
								<th scope="col">Finish Date</th>
								<th scope="col">Enabled</th>
								<th scope="col">Save</th>
								<th scope="col">Delete</th>
							</tr>
							</thead>
							<tbody id="link_list">
							<tr>
								<td><img src="images/loading.gif"></td>
							</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
			<div class="col-sm-2">
			
				<%@include file="_userSelection.jsp" %>
			
			</div>
		</div>
	</div>

	<%@include file="_footer.jsp"%>
	
	<%@include file="_modalConfirmForm.jsp"%>

<script type="application/javascript">
var uname = "<c:out value='${login}' />";

function changeSelectedUserImpl(item){
	uname = $(item).val();
	$('#header_username').html(uname);
	
	getLinks('/link/'+uname+'/free','link_list');
	getRootLinks('/rootlinks/getActual', 'rootLinks');
			
	jcaUtils.clearValues(['target'], 'val');
}
</script>
</body>
</html>

