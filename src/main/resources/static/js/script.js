
const toggleSidebar =()=>{
    if($(".sidebar").is(":visible")){
     
        $(".sidebar").css("display","none")
        $("#bars").css("color","red")
        $(".contents").css("margin-left","0%")
    }
    else{
        $(".sidebar").css("display","block")
        $("#bars").css("color","green")
        $(".contents").css("margin-left","20%")
    }
} 

const search=()=>{
    let query =$("#search-input").val();
    
  
    if(query==''){
    
        $(".search-result").hide();

    }else{
     
       // search 
        console.log(query);

      // Sending Request To Server...
      
      let url =`http://localhost:8080/search/${query}`;

        fetch(url).then((response)=>{
            return response.json();

        }).then((data)=>{

            console.log(data);

            let text =`<div class='list-group'>`;

            data.forEach(contact => {
                text+=` <a href='/user/${contact.cId}/contact/' class='list-group-item list-group-item-action text-left'>${contact.name}</a>`
            });

            text+=`</div>`;
            $(".search-result").html(text);
            $(".search-result").show();
        });
       
      
    }


};