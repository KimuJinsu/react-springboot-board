// src/component/noticeboard/ModifyPost.js
import React, { useState, useEffect } from 'react';
import axios from '../../utils/axiosConfig';
import { useParams, useNavigate } from 'react-router-dom';

const ModifyPost = ({ idStatus }) => { // idStatus를 프롭스로 받음
    const { id } = useParams(); // URL에서 postId 추출
    const navigate = useNavigate();
    const [post, setPost] = useState(null);
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [tags, setTags] = useState([]);
    const [deletedFileIds, setDeletedFileIds] = useState([]);
    const [files, setFiles] = useState([]);

    useEffect(() => {
        const fetchPost = async () => {
            try {
                const response = await axios.get(`/posts/${id}`);
                const postData = response.data;

                if (postData.writerUsername !== idStatus) {
                    alert("글 수정은 작성자만 할 수 있습니다.");
                    navigate(`/posts/${id}`);
                } else {
                    setPost(postData);
                    setTitle(postData.title);
                    setContent(postData.content);
                    setTags(postData.tags || []);
                }
            } catch (e) {
                alert(e.response?.data?.message || "오류가 발생했습니다.");
                navigate('/noticelist');
            }
        };
        fetchPost();
    }, [id, idStatus, navigate]);

    const handleTagChange = (e) => {
        const inputTags = e.target.value.split(',').map(tag => tag.trim());
        setTags(inputTags);
    };

    const handleFileChange = (e) => {
        setFiles([...e.target.files]);
    };

    const handleDeleteFile = (fileId) => {
        setDeletedFileIds(prevIds => [...prevIds, fileId]);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const formData = new FormData();
            formData.append('postRequest', new Blob([JSON.stringify({
                title,
                content,
                privatePost: post.privatePost,
                blockComment: post.blockComment,
                tags,
                deletedFileIds
            })], { type: 'application/json' }));

            if (files && files.length > 0) {
                for (let file of files) {
                    formData.append('multipartFiles', file);
                }
            }

            // 디버깅용 콘솔 로그
            for (let pair of formData.entries()) {
                console.log(`${pair[0]}:`, pair[1]);
            }

            // 'Content-Type' 헤더 제거: axios가 자동으로 설정하도록 함
            await axios.put(`/posts/${id}`, formData);
            alert("게시글이 수정되었습니다.");
            navigate(`/post/${id}`); // 수정 완료 후 PostView로 이동
        } catch (e) {
            console.error(e);
            alert(e.response?.data?.message || "수정에 실패했습니다.");
        }
    };

    if (!post) {
        return <div>Loading...</div>;
    }

    return (
        <section className="modifyPostFrame">
            <div className="modifyPostSection">
                <h2>게시글 수정</h2>
                <form onSubmit={handleSubmit} encType="multipart/form-data">
                    <div className="form-group">
                        <label>제목</label>
                        <input
                            type="text"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            required
                            className="form-control"
                        />
                    </div>
                    <div className="form-group">
                        <label>내용</label>
                        <textarea
                            value={content}
                            onChange={(e) => setContent(e.target.value)}
                            required
                            className="form-control"
                            rows="10"
                        />
                    </div>
                    <div className="form-group">
                        <label>태그 (쉼표로 구분)</label>
                        <input
                            type="text"
                            value={tags.join(', ')}
                            onChange={handleTagChange}
                            className="form-control"
                        />
                    </div>
                    <div className="form-group">
                        <label>첨부파일 추가</label>
                        <input
                            type="file"
                            multiple
                            onChange={handleFileChange}
                            className="form-control"
                        />
                    </div>
                    <div className="form-group">
                        <label>첨부파일 삭제</label>
                        {post.attachments && post.attachments.length > 0 && (
                            <ul>
                                {post.attachments.map(file => (
                                    <li key={file.id}>
                                        {file.realFileName}
                                        <button
                                            type="button"
                                            onClick={() => handleDeleteFile(file.id)}
                                            className="btn btn-danger btn-sm ml-2"
                                        >
                                            삭제
                                        </button>
                                    </li>
                                ))}
                            </ul>
                        )}
                    </div>
                    <button type="submit" className="btn btn-primary">수정 완료</button>
                </form>
            </div>
        </section>
    );
};

export default ModifyPost;
