import axios from "../../utils/axiosConfig";
import React, { useEffect, useState, useRef } from "react";
import Postcontent from "./postlistcontent";
import PostPointer from "./postpagenation";
import qs from 'qs';
import { Link, useSearchParams } from "react-router-dom";

const NoticeList = () => {
    const [searchParams] = useSearchParams();
    const query = qs.parse(window.location.search, { ignoreQueryPrefix: true });

    const maxPages = useRef(1);
    const [postAll, setPostAll] = useState([]); // 게시글 데이터
    const [pages, setPages] = useState(1); // 현재 페이지
    const [totalPost, setTotalPost] = useState(0); // 게시글 총 개수

    // 검색 엔터 처리
    const searchDataInput = async (e) => {
        if (e.key === "Enter") {
            if (e.target.value !== '') {
                window.location.replace(`/noticelist?type=search&data=${e.target.value}`);
                setPages(1); // 페이지 초기화
            } else {
                getPost();
            }
        }
    };

    // 게시글 데이터 가져오기
    const getPost = async () => {
        try {
            const response = await axios({
                method: "GET",
                mode: "cors",
                url: `/posts?page=${pages - 1}&size=10`,
            });

            if (response?.data) {
                console.log("API 응답 데이터:", response.data); // 디버깅 로그
                setPostAll(response.data); // 게시글 데이터 설정
            } else {
                console.error("API 응답 형식이 예상과 다릅니다:", response);
                setPostAll([]);
            }

            // 게시글 총 개수 가져오기
            const countResponse = await axios({
                method: "GET",
                mode: "cors",
                url: `/posts/count?type=normal`,
            });

            setTotalPost(countResponse?.data?.count || 0);
        } catch (error) {
            console.error("게시글 불러오는 중 오류 발생:", error);
            alert("게시글을 불러올 수 없습니다. 다시 시도해주세요.");
        }
    };

    // 페이지 설정 함수
    const setNowPages = async (value) => {
        setPages(value);
    };

    // 태그 검색
    const tagSearch = async () => {
        if (!query.tag) {
            window.location.replace("/noticelist");
            return;
        }

        try {
            const response = await axios({
                method: "GET",
                mode: "cors",
                url: `/posts/search/tags/${searchParams.get("data")}?page=${pages - 1}&size=10`,
            });

            setPostAll(response?.data || []);

            const countResponse = await axios({
                method: "GET",
                mode: "cors",
                url: `/posts/count?type=tag&data=${searchParams.get("data")}`,
            });

            setTotalPost(countResponse?.data?.count || 0);
        } catch (error) {
            console.error("태그 검색 중 오류 발생:", error);
            alert("태그 검색에 실패했습니다.");
        }
    };

    // useEffect로 데이터 로드
    useEffect(() => {
        const type = searchParams.get("type");
        if (type === "search") {
            const searchKeyword = searchParams.get("data");
            axios({
                method: "GET",
                mode: "cors",
                url: `/posts/search/${searchKeyword}?page=${pages - 1}&size=10`,
            })
                .then((response) => {
                    setPostAll(response?.data || []);
                    return axios({
                        method: "GET",
                        mode: "cors",
                        url: `/posts/count?type=search&data=${searchKeyword}`,
                    });
                })
                .then((countResponse) => setTotalPost(countResponse?.data?.count || 0))
                .catch((error) => {
                    console.error("검색 중 오류 발생:", error);
                    alert("검색에 실패했습니다.");
                });
        } else if (type === "tag") {
            tagSearch();
        } else {
            getPost();
        }
    }, [pages, searchParams]);

    // 페이지 이동 함수
    const gotoNext = () => {
        if (pages < maxPages.current) setPages(pages + 1);
    };

    const gotoPrevious = () => {
        if (pages > 1) setPages(pages - 1);
    };

    const pushData = () => {
        maxPages.current = Math.ceil(totalPost / 10);
        const arrs = [];
        for (let i = 1; i <= maxPages.current; i++) {
            arrs.push(i);
        }
        return <PostPointer pages={arrs} nowPage={pages} setPage={setNowPages} />;
    };

    return (
        <section className="sectionArea setresponsible">
            <article className="noticeFrameArea setresponsiblearticle">
                <div className="freeNotice">
                    <div className="pagedesc">게시판</div>
                    <div className="searchCoordinate">
                        <input
                            type="text"
                            className="searchingArea"
                            placeholder="검색할 제목 및 내용"
                            onKeyDown={searchDataInput}
                        />
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            fill="currentColor"
                            className="bi bi-search search-icon"
                            viewBox="0 0 16 16"
                        >
                            <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z" />
                        </svg>
                    </div>
                </div>
                <div className="PostArea">
                    <div className="noticedescription">
                        <span id="DescHead">번호</span>
                        <span id="DescHead">제목</span>
                        <span id="DescHead">작성자</span>
                        <span id="DescHead">작성일</span>
                        <span id="DescHead">추천</span>
                        <span id="DescHead">조회수</span>
                    </div>
                    {postAll.length === 0 ? (
                        <div>게시글이 없습니다.</div>
                    ) : (
                        <Postcontent data={postAll} />
                    )}
                </div>
                <nav aria-label="Page navigation example" className="pagenations">
                    <ul className="pagination">
                        <li className="page-item">
                            <a className="page-link" href="#" onClick={gotoPrevious} style={{ boxShadow: "none" }}>
                                &lt;
                            </a>
                        </li>
                        {pushData()}
                        <li className="page-item">
                            <a className="page-link" href="#" onClick={gotoNext} style={{ boxShadow: "none" }}>
                                &gt;
                            </a>
                        </li>
                    </ul>
                    <Link to={"/newpost"} className="newPost_post">
                        New Post
                    </Link>
                </nav>
            </article>
        </section>
    );
};

export default React.memo(NoticeList);

// import axios from "../../utils/axiosConfig";
// import React, { useEffect, useState, useRef } from "react";
// import Postcontent from "./postlistcontent";
// import PostPointer from "./postpagenation";
// import qs from 'qs';
// import { Link, useSearchParams } from "react-router-dom";

// const NoticeList = () => {
//     const [searchParams , setSearchParams] = useSearchParams();

//     const query = qs.parse(window.location.search, { // ?tag=데이터 로 찾음 query.tag
//         ignoreQueryPrefix: true
//     });
    
//     const maxPages = useRef(1);
//     const [postAll, setPostAll] = useState([]); // 모든 post
//     const [pages, setPages] = useState(1); // 현재 페이지
//     const [totalPost, setTotalPost] = useState(0); // 전체 post
    
//     const searchDataInput = async (e) => {
//         if (e.key == 'Enter') {
//             if (e.target.value !== '') {
//                 window.location.replace(`/noticelist?type=search&data=${e.target.value}`);
//                 setPages(0);
//             } else {
//                 getPost();
//             }
//         }
//     }

//     // const getPost = async () => { // 일반
//     //     const getPostResult = await axios({ // 게시판 데이터 가져오기
//     //         method: "GET",
//     //         mode: "cors",
//     //         url: `/posts?page=${pages - 1}&size=10`,
//     //     });
        
//     //     console.log("API Response:", getPostResult.data.data); // API 응답 데이터 확인

//     //     setPostAll(getPostResult.data.data); // 전체 데이터

//     //     const result = await axios({
//     //         method: "GET",
//     //         mode: "cors",
//     //         url: `/posts/count?type=normal`
//     //     });
//     //     setTotalPost(result.data.data);
//     // };
//     const getPost = async () => {
//         try {
//             const response = await axios({
//                 method: "GET",
//                 mode: "cors",
//                 url: `/posts?page=${pages - 1}&size=10`,
//             });

//             if (response?.data?.data) {
//                 console.log("게시글 데이터:", response.data.data);
//                 setPostAll(response.data.data); // 전체 데이터 저장
//             } else {
//                 console.error("API 응답이 예상과 다릅니다:", response.data);
//                 setPostAll([]);
//             }

//             const countResponse = await axios({
//                 method: "GET",
//                 mode: "cors",
//                 url: `/posts/count?type=normal`,
//             });

//             setTotalPost(countResponse?.data?.data || 0);
//         } catch (error) {
//             console.error("데이터를 불러오는 중 오류 발생:", error);
//             alert("게시글을 불러오지 못했습니다. 다시 시도해주세요.");
//         }
//     };


//     const tagSearch = async () => { // 태그
//         if (query.tag === '') {
//             window.location.replace("/noticelist");
//             return;
//         } else {
//             await axios({
//                 method: "GET",
//                 mode: "cors",
//                 url: `/posts/search/tags/${searchParams.get("data")}?page=${pages - 1}&size=10`
//             })
//             .then((response) => { setPostAll(response.data.data) }) 
//             .catch((e) => alert(e.response.data.message));

//             await axios({
//                 method: "GET",
//                 mode: "cors",
//                 url: `/posts/count?type=tag&data=${searchParams.get("data")}`
//             })
//             .then((response) => { setTotalPost(response.data.data) }) 
//             .catch((e) => alert(e.response.data.message));
//         }
//     }

//     const setNowPages = async (value) => {
//         setPages(value);
//     }

//         useEffect(async () => {
//             if(searchParams.get("type") == "search") { // 일반 검색
//                 await axios({ // 게시판 데이터 가져오기
//                     method: "GET",
//                     mode: "cors",
//                     url: `/posts/search/${searchParams.get("data")}?page=${pages - 1}&size=10`,
//                 })
//                 .then(async (response) => { 
//                     setPostAll(response.data.data)
//                     await axios({
//                         method: "GET",
//                         mode: "cors",
//                         url: `/posts/count?type=search&data=${searchParams.get("data")}`
//                     })
//                     .then((response) => { setTotalPost(response.data.data) }) 
//                     .catch((e) => alert(e.response.data.message));
//                  }) 
//                 .catch((e) => alert(e.response.data.message));
//             }
//             else if (searchParams.get("type") == "tag") {
//                 tagSearch();
//             }
//             else {
//                 getPost();
//             }

//     }, [pages]);

//     const gotoNext = () => {
//         if (pages < maxPages.current) {
//             setPages(parseInt(pages) + 1);
//         }
//     }

//     const gotoPrevious = () => {
//         if (pages > 1) {
//             setPages(parseInt(pages) - 1);
//         }
//     }

//     const pushData = () => {
//         maxPages.current = Math.ceil(totalPost / 10);
//         const arrs = [];
//         for (let i = 1; i <= maxPages.current; i++) {
//             arrs.push([i]);
//         }
//         return <PostPointer pages={arrs} nowPage={pages} setPage={setNowPages} />
//     }

//     return (
//         <section className="sectionArea setresponsible">
//             <article className="noticeFrameArea setresponsiblearticle">
//                 <div className="freeNotice">
//                     <div className="pagedesc">게시판</div>
//                     <div className="searchCoordinate">
//                         <input type="text" className="searchingArea" placeholder='검색할 제목 및 내용' onKeyDown={searchDataInput} />
//                         <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" className="bi bi-search search-icon" viewBox="0 0 16 16">
//                             <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z" />
//                         </svg>
//                     </div>
//                 </div>
//                 <div className="PostArea">
//                     <div className="noticedescription">
//                         <span id="DescHead">번호</span>
//                         <span id="DescHead">제목</span>
//                         <span id="DescHead">작성자</span>
//                         <span id="DescHead">작성일</span>
//                         <span id="DescHead">추천</span>
//                         <span id="DescHead">조회수</span>
//                     </div>

                    
                    
                    
//                     <Postcontent data={postAll} />
//                 </div>
//                 <nav aria-label="Page navigation example" className="pagenations">
//                     <ul className="pagination">
//                         <li className="page-item"><a className="page-link" href="#" onClick={gotoPrevious} style={{ boxShadow: "none" }}>&lt;</a></li>
//                         {pushData()}
//                         <li className="page-item"><a className="page-link" href="#" onClick={gotoNext} style={{ boxShadow: "none" }}>&gt;</a></li>
//                     </ul>
//                     <Link to={'/newpost'} className="newPost_post">New Post</Link>
//                 </nav>
//             </article>
//         </section>
//     )
// }

// export default React.memo(NoticeList);