import React from "react";
import styled from "styled-components";
import Box from '@mui/material/Box';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Grid from '@mui/material/Unstable_Grid2';
import SearchParams from '../models/searchParams.js'
import {movieSearch} from "backend/movie";
import {useUser} from "hook/User";
import {detailMovieSearch} from "backend/movie";
import Card from "@mui/material/Card";
import CardMedia from "@mui/material/CardMedia";
import Typography from "@mui/material/Typography";
import { CardActionArea } from "@mui/material";

import { useNavigate } from "react-router-dom";


const Search = () => {
    const {
        accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();
    const [movies, setMovies] = React.useState([]);

    const [searchButtonClicked,setSearchButtonClicked] = React.useState(false);

    const submitForm = () =>{          
        var query = new SearchParams();

        if (title){
            query.setTitle(title);
        }

        if (year){
          query.setYear(year);
        }


        if (genre){
          query.setGenre(genre);
        }


        if (director){
          query.setDirector(director);
        }

        query.setLimit(limit);
        query.setOrderBy(orderBy);
        query.setDirection(direction);

        var response = movieSearch(accessToken,query);

        response.then(value => {
          console.log(value.data.movies)
          setMovies(value.data.movies)});

        // movies.map(movie => {
        //   movie.backdropPath = "https://image.tmdb.org/t/p/original"+movie.backdropPath
        //   movie.posterPath = "https://image.tmdb.org/t/p/original"+movie.posterPath

        // }
        // );
        const updatedMovies = movies.map(movie => ({
          ...movie,
          backdropPath: "https://image.tmdb.org/t/p/original" + movie.backdropPath,
          posterPath: "https://image.tmdb.org/t/p/original" + movie.posterPath
        }));
    
        setMovies(updatedMovies);
        setSearchButtonClicked(true);
        
      
    };


    const navigate = useNavigate();

  

   // This variable gets the input of the title/(keywords)
    const [userInput, setUserInput] = React.useState();


    // Start of the query "baiscally tittle"
    const [title, setTitle] = React.useState();



    const [year, setYear] = React.useState();
    const [genre, setGenre] = React.useState();
    const [director, setDirector] = React.useState();

    
    const [direction, setDirection] = React.useState("asc");

    const [orderBy, setOrderBy] = React.useState("title");

    const [limit, setLimit] = React.useState(10);


    const [selectedMovieId, setSelectedMovieId] = React.useState(-1);


    // const selectOption = (event) => {
    //   setOption(event.target.value);
    // };

    const handleDirection = (event) => {
        setDirection(event.target.value);
    };
    
    const handleOrderBy = (event) => {
        setOrderBy(event.target.value);
    };

    const handleLimit = (event) => {
        setLimit(event.target.value);
    };


    const handleView =(id) =>{

    var response = detailMovieSearch(accessToken,id);
    response.then(value =>{ 
      
      navigate("/movie/"+id,{state:value.data});
 
    });
   
    }

    const columns = [
        { field: 'id',   sortable: false, headerName: 'Id', width: 100 },
        { field: 'title',sortable: false,headerName: 'Title', width: 250 },
        { field: 'year', sortable: false,headerName: 'Year', width: 70 },
        { field: 'director',   sortable: false, headerName: 'Director', width: 200 },
        { field: 'rating',   sortable: false, headerName: 'Rating', width: 70 },
        { field: 'backdropPath',sortable: false,headerName: 'Backdrop Path', width: 130 },
        { field: 'posterPath', sortable: false,headerName: 'Poster Path', width: 130 },
      ];

    return (
        <>
        <div>
        <div style={{margin:0,display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
        <Box sx={{flexGrow: 1, marginTop:20,maxWidth:1000}}>
      <Grid container spacing={4}>
        <Grid xs={8}>
        <FormControl variant="standard">
          <TextField
            sx={{width:600}}
            value={userInput}
            onChange={(e) => setTitle(e.target.value)}
            id="input-with-icon-textfield"
            label="Search for Movie Titles"
            variant="standard"
          />
        </FormControl>
        </Grid>
        <Grid xs={4}>
          <Button onClick = {submitForm} sx={{marginTop:2, minWidth: 100, maxHeight: 50 }} variant="contained">Search</Button>
        </Grid>

       

        <Grid xs={4}>
        <FormControl fullWidth>
        <InputLabel id="demo-simple-select-label">Order By</InputLabel>
        <Select
          labelId="demo-simple-select-label"
          id="demo-simple-select"
          value={orderBy}
          label="Order By"
          onChange={handleOrderBy}
        >
          <MenuItem value={"title"}>Title</MenuItem>
          <MenuItem value={"rating"}>Rating</MenuItem>
          <MenuItem value={"year"}>Year</MenuItem>
        </Select>
        </FormControl>
        </Grid>
        <Grid xs={4}>
        <FormControl fullWidth>
        <InputLabel id="demo-simple-select-label">Direction</InputLabel>
        <Select
          labelId="demo-simple-select-label"
          id="demo-simple-select"
          value={direction}
          label="Direction"
          onChange={handleDirection}
        >
          <MenuItem value={"asc"}>Ascending</MenuItem>
          <MenuItem value={"desc"}>Descending</MenuItem>
        </Select>
        </FormControl>
        </Grid>
        <Grid xs={4}>
        <FormControl fullWidth>
        <InputLabel id="demo-simple-select-label">Limit</InputLabel>
        <Select
          labelId="demo-simple-select-label"
          id="demo-simple-select"
          value={limit}
          label="limit"
          onChange={handleLimit}
        >
          <MenuItem value={10}>10</MenuItem>
          <MenuItem value={25}>25</MenuItem>
          <MenuItem value={50}>50</MenuItem>
          <MenuItem value={100}>100</MenuItem>
        </Select>
        </FormControl>
        </Grid>


        <Grid xs={3}>
        <FormControl variant="standard">
          <TextField
            value={year}
            onChange={(e) => setYear(e.target.value)}
            id="input-with-icon-textfield"
            label="Year"
            variant="standard"
          />
        </FormControl>
        </Grid>
        <Grid xs={3}>
        <FormControl variant="standard">
          <TextField
            value={genre}
            onChange={(e) => setGenre(e.target.value)}
            id="input-with-icon-textfield"
            label="Genre"
            variant="standard"
          />
        </FormControl>
        </Grid>
        <Grid xs={3}>
        <FormControl variant="standard">
          <TextField
            value={director}
            onChange={(e) => setDirector(e.target.value)}
            id="input-with-icon-textfield"
            label="Director"
            variant="standard"
          />
        </FormControl>
        </Grid>

      </Grid>
    </Box>
    </div>



        {/* <Box sx={{ minWidth: 120 }}>
        <FormControl fullWidth>
        <InputLabel id="demo-simple-select-label">Options</InputLabel>
        <Select
          labelId="demo-simple-select-label"
          id="demo-simple-select"
          value={option}
          label="Option"
          onChange={selectOption}
        >
          <MenuItem value={"title"}>Title</MenuItem>

           <MenuItem value={"year"}>Year</MenuItem>
          <MenuItem value={"director"}>Director</MenuItem>
          <MenuItem value={"genre"}>Genre</MenuItem> 
        </Select>
        </FormControl>
        </Box> */}








{searchButtonClicked && (
        <div style={{margin:0,display: 'flex', justifyContent: 'center', alignItems: 'center'}}>
        <Box sx={{flexGrow: 1,marginLeft:-10,marginTop:20, maxWidth:900}}>
          <Grid container spacing={10}>
            {movies.map(movie => (
              <Grid xs={3}>
                <div className="hover">
                  <Card sx={{ marginTop:-3,maxHeight: 300,width: 200,borderRadius:5,border:"3px solid #031f36" }}>
                          <Typography sx ={{pt:1,fontWeight:'bold', fontSize:15,textAlign:'center'}} gutterBottom variant="h6" component="div">
                          {movie.title}
                          </Typography>
                          <CardActionArea onClick = {() => handleView(movie.id)}>
                            <CardMedia
                              component="img"
                              height="270"
                              image={'https://image.tmdb.org/t/p/original'+movie.posterPath}
                              alt="Spooder-Man No Way Home"
                            />
                          </CardActionArea>
                        </Card>
                  </div>
              </Grid>
            ))}
          </Grid>
          </Box>
        </div>
      )}
          
            {/* { searchButtonClicked &&  <DataGrid
                rows={movies}
                sx = {{mt:10}}
                columns={columns}
                pageSize={5}
                rowsPerPageOptions={[5]}
                onSelectionModelChange={(move_id) => {
                  setSelectedMovieId(move_id[0]);
                }}
                />} */}
                


</div>
        </>
    );
}

export default Search;
