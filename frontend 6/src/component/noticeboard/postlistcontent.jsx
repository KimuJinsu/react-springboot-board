import React from "react";
import { Link } from "react-router-dom";
import defaultALT from '../../_image/defaultALT.png'

const Postcontent = ({ data }) => {

    if (data === null || data.length === 0) {
        return (
            <div>
                게시글이 없습니다.
            </div>
        );
    }

    return (
        data.map(post => (
            <Link to={`/viewpost/${post.id}`} className="linktopost" key={post.id}>
                <div className="noticedescription userPost">
                    {/* 번호 */}
                    <span>{post.id}</span>

                    {/* 제목 */}
                    <span>
                        {post.title} 
                        <div style={{ color: "rgb(60,172,255)", display: "inline" }}>
                            {post.count ? `[${post.count}] ` : null}
                        </div>
                    </span>

                    {/* 작성자 정보 */}
                    <span>
                        <img 
                            src={post.writerProfileImageUrl || defaultALT} 
                            className="profileImageData" 
                            alt="프로필 이미지" 
                        /> 
                        {post.writerUsername || "알 수 없는 사용자"}
                    </span>

                   {/* 작성일 */}
                   <span>
                        {
                            post.postDate 
                                ? new Date(post.postDate).toLocaleString('ko-KR', { 
                                    year: 'numeric', 
                                    month: '2-digit', 
                                    day: '2-digit', 
                                    hour: '2-digit', 
                                    minute: '2-digit' 
                                  }) 
                                : "날짜 없음"
                        }
                    </span>

                    {/* 추천 */}
                    <span>{post.likes || 0}</span>

                    {/* 조회수 */}
                    <span>{post.views || 0}</span>
                </div>
            </Link>
        ))
    );
};

export default React.memo(Postcontent);

// import React from "react";
// import { Link } from "react-router-dom";
// import defaultALT from '../../_image/defaultALT.png'

// const Postcontent = ({ data }) => {

//     if(data === null) {
//         return(
//             <div>

//             </div>
//         )
//     }

//     return (
//         data.map(posts => (
//             <Link to={`/viewpost/${posts.numbers}`} className="linktopost" key={posts.numbers}>
//                 <div className="noticedescription userPost">
//                     <span>{posts.numbers}</span>
//                     <span>{posts.title} <div style={{color: "rgb(60,172,255)", display:"inline"}}>{posts.count ? "["+ posts.count + "] ": null}</div></span>
//                     <span><img src={posts.writerImage || defaultALT} className="profileImageData" alt="img"/> {posts.writerIsDelete ? "탈퇴한 사용자" : posts.writer}</span>
//                     <span>{posts.postDate}</span>
//                     <span>{posts.likes}</span>
//                     <span>{posts.views}</span>
//                 </div>
//             </Link>
//         ))
//     );
// };

// export default React.memo(Postcontent);